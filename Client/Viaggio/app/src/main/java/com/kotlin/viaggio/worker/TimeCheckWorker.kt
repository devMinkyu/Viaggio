package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import java.util.*
import javax.inject.Inject

class TimeCheckWorker @Inject constructor(context: Context, parameters: WorkerParameters) :
    BaseWorker(context, parameters) {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    override fun doWork(): Result {
        super.doWork()
        val cal = Calendar.getInstance()
        val lastConnectOfDay = prefUtilService.getInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY).blockingGet()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).blockingAwait()
        if ((currentConnectOfDay - lastConnectOfDay) != 0) {
            var travelingOfDayOfCount =
                prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet()
            travelingOfDayOfCount += 1
            prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, travelingOfDayOfCount)
                .blockingAwait()
        }
        return Result.success()
    }
}