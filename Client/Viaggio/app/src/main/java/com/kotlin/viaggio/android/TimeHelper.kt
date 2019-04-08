package com.kotlin.viaggio.android

import android.annotation.SuppressLint
import android.content.Context
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.RxEventBus
import com.kotlin.viaggio.model.TravelLocalModel
import dagger.Lazy
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TimeHelper @Inject constructor(){
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>
    @Inject
    lateinit var rxEventBus: RxEventBus

    @SuppressLint("SimpleDateFormat")
    fun timeCheckOfDay(){
        val cal = Calendar.getInstance()
        val lastConnectOfDay = prefUtilService.getInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY).blockingGet()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).blockingAwait()

        if ((currentConnectOfDay - lastConnectOfDay) != 0) {
            var travelingOfDayOfCount =
                prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet()
            travelingOfDayOfCount += 1
            prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, travelingOfDayOfCount).blockingAwait()

            val travelOfDay = TravelOfDay(dayCountries = arrayListOf(prefUtilService.getString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES).blockingGet())
            ,travelOfDay = travelingOfDayOfCount, travelId = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID).blockingGet()
            )

            val travelOfDayId = travelLocalModel.createTravelOfDay(travelOfDay).subscribeOn(Schedulers.io()).blockingGet()
            prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ID, travelOfDayId).blockingAwait()
            travelOfDay.id = travelOfDayId
            rxEventBus.travelOfDayChange.onNext(true)
        }
    }
}