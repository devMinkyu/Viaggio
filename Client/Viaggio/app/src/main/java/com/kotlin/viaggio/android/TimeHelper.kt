package com.kotlin.viaggio.android

import android.annotation.SuppressLint
import android.content.Context
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.RxEventBus
import com.kotlin.viaggio.model.TravelModel
import dagger.Lazy
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TimeHelper @Inject constructor(){
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var travelModel: TravelModel
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>
    @Inject
    lateinit var rxEventBus: RxEventBus

    @SuppressLint("SimpleDateFormat")
    fun timeCheckOfDay(){
        val cal = Calendar.getInstance()
        val lastConnectOfDay = prefUtilService.getInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY).blockingGet()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).observeOn(Schedulers.io()).subscribe()

        if ((currentConnectOfDay - lastConnectOfDay) != 0) {
            var travelingOfDayOfCount =
                prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet()
            travelingOfDayOfCount += 1
            prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, travelingOfDayOfCount).observeOn(Schedulers.io()).subscribe()


            val day = SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).format(cal.time)
            val travelOfDay = TravelOfDay(dayCountries = arrayListOf(prefUtilService.getString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES).blockingGet())
            ,travelOfDay = travelingOfDayOfCount, travelId = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID).blockingGet()
            ,date = SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).parse(day))

            val travelOfDayId = travelModel.createTravelOfDay(travelOfDay).subscribeOn(Schedulers.io()).blockingGet()
            prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ID, travelOfDayId).observeOn(Schedulers.io()).subscribe()
            travelOfDay.id = travelOfDayId
            rxEventBus.travelOfDayChange.onNext(true)
        }
    }
}