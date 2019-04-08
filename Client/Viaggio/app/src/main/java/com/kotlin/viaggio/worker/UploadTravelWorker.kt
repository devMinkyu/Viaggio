package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.model.TravelModel
import javax.inject.Inject

class UploadTravelWorker(context: Context, params:WorkerParameters):BaseWorker(context, params){
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var travelModel: TravelModel
    override fun doWork(): Result {
        super.doWork()
        val toJson = inputData.getString(WorkerName.TRAVEL.name)?:""
        val travel = gson.fromJson(toJson, Travel::class.java)

        val disposable = travelModel.uploadTravel(travel)
            .andThen {
                travel.userExist = true
                travelLocalModel.updateTravel(travel)
            }
            .subscribe()
        disposable.dispose()
        return Result.success()
    }
}