package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.google.gson.Gson
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import io.reactivex.Completable
import timber.log.Timber
import java.io.File
import java.lang.Exception
import javax.inject.Inject

class UploadTravelWorker @Inject constructor(context: Context, params: WorkerParameters) : BaseWorker(context, params) {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var transferUtility: TransferUtility

    override fun doWork(): Result {
        super.doWork()
        val toJson = inputData.getString(WorkerName.TRAVEL.name) ?: ""
        val travel = gson.fromJson(toJson, Travel::class.java) ?: Travel()

        val toJson1 = inputData.getString(WorkerName.TRAVEL_CARD.name) ?: ""
        val travelCard = gson.fromJson(toJson1, TravelCard::class.java) ?: TravelCard()

        if(travelCard.imageNames.isNotEmpty()){
            val imageUris = travelCard.imageNames.map {
                val uploadObserver = transferUtility.upload(BuildConfig.S3_UPLOAD_BUCKET, it, File(it))
                uploadObserver.setTransferListener(object :TransferListener{
                        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                        override fun onStateChanged(id: Int, state: TransferState?) {}
                        override fun onError(id: Int, ex: Exception?) {
                            Timber.d(it)
                        }
                    })
                if (uploadObserver.state == TransferState.COMPLETED) {
                    uploadObserver.absoluteFilePath
                }else{
                    ""
                }
            }
        }

        if(travel.id != 0L){
            travelModel.uploadTravel(travel)
                .flatMapCompletable {
                    if(it.isSuccessful){
                        travel.userExist = true
                        travelLocalModel.updateTravel(travel)
                    }else{
                        travelModel.uploadTravel(travel)
                            .flatMapCompletable {sec ->
                                if(sec.isSuccessful){
                                    travel.userExist = true
                                    travelLocalModel.updateTravel(travel)
                                }else{
                                    // 처리 부
                                    Completable.complete()
                                }
                            }
                    }
                }.blockingAwait()
        }

        if(travelCard.id != 0L){
            travelModel.uploadTravelCard(travelCard)
                .flatMapCompletable {
                    if(it.isSuccessful){
                        travelCard.userExist = true
                        travelLocalModel.updateTravelCard(travelCard)
                    }else{
                        Completable.complete()
                    }
                }.blockingAwait()
        }

        return Result.success()
    }
}