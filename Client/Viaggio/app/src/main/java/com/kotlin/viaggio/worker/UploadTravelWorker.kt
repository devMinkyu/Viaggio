package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import io.reactivex.Completable
import javax.inject.Inject

class UploadTravelWorker @Inject constructor(context: Context, params: WorkerParameters) : BaseWorker(context, params) {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var travelModel: TravelModel
    override fun doWork(): Result {
        super.doWork()
        val toJson = inputData.getString(WorkerName.TRAVEL.name) ?: ""
        val travel = gson.fromJson(toJson, Travel::class.java)

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
        return Result.success()
    }
}