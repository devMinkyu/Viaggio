package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.google.gson.Gson
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.model.UserModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
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
    @Inject
    lateinit var config: DeveloperAuthenticationProvider
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var userModel: UserModel

    override fun doWork(): Result {
        super.doWork()
        val toJson = inputData.getString(WorkerName.TRAVEL.name) ?: ""
        val travel = gson.fromJson(toJson, Travel::class.java) ?: Travel()

        val toJson1 = inputData.getString(WorkerName.TRAVEL_CARD.name) ?: ""
        val travelCard = gson.fromJson(toJson1, TravelCard::class.java) ?: TravelCard()

        if (travel.localId != 0L) {
            travelModel.uploadTravel(travel)
                .flatMapCompletable {
                    if (it.isSuccessful) {
                        travel.userExist = true
                        travel.serverId = it.body()?.id ?: 0
                        travelLocalModel.updateTravel(travel)
                    } else {
                        Completable.complete()
                    }
                }.blockingAwait()
        }

        if (travelCard.localId != 0L) {
            if (travelCard.imageNames.isNotEmpty()) {
                userModel.getAws()
                    .flatMapCompletable {
                        val list = travelCard.imageNames.map {
                            Single.create<String> { emitter ->
                                val awsId = prefUtilService.getString(AndroidPrefUtilService.Key.AWS_ID).blockingGet()
                                val awsToken =
                                    prefUtilService.getString(AndroidPrefUtilService.Key.AWS_TOKEN).blockingGet()
                                config.setInfo(awsId, awsToken)
                                val uploadObserver = transferUtility.upload(BuildConfig.S3_UPLOAD_BUCKET, "image/${it.split("/").last()}", File(it))
                                uploadObserver.setTransferListener(object : TransferListener {
                                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                                    override fun onStateChanged(id: Int, state: TransferState?) {
                                        if (state == TransferState.COMPLETED) {
                                            emitter.onSuccess(uploadObserver.key)
                                        }
                                    }
                                    override fun onError(id: Int, ex: Exception?) {
                                    }
                                })
                            }.subscribeOn(Schedulers.io())
                        }
                        val resultList = mutableListOf<String>()

                        Single.merge(list)
                            .map {
                                resultList.add(it)
                            }.lastOrError()
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .flatMapCompletable {
                                travelCard.imageUrl = resultList
                                travelModel.uploadTravelCard(travelCard)
                                    .flatMapCompletable {
                                        if (it.isSuccessful) {
                                            travelCard.userExist = true
                                            travelCard.serverId = it.body()?.id ?: 0
                                            travelLocalModel.updateTravelCard(travelCard)
                                        } else {
                                            Completable.complete()
                                        }
                                    }
                            }
                    }
            } else {
                travelModel.uploadTravelCard(travelCard)
                    .flatMapCompletable {
                        if (it.isSuccessful) {
                            travelCard.userExist = true
                            travelCard.serverId = it.body()?.id ?: 0
                            travelLocalModel.updateTravelCard(travelCard)
                        } else {
                            Completable.complete()
                        }
                    }
            }.blockingAwait()
        }
        return Result.success()
    }
}