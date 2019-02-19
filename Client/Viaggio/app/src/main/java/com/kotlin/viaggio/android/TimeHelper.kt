package com.kotlin.viaggio.android

import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeHelper @Inject constructor(){
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    fun timeCheckOfDay(){
        val cal = Calendar.getInstance()
        val lastConnectOfDay = prefUtilService.getInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY).blockingGet()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).subscribe()

        if ((currentConnectOfDay - lastConnectOfDay) != 0) {
            var travelingOfDayOfCount =
                prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet()
            travelingOfDayOfCount += 1
            prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, travelingOfDayOfCount).subscribe()
        }
    }
}