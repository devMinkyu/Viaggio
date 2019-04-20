package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor() : BaseModel() {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var api: ViaggioApiService

    fun uploadTravel(travel:Travel):Completable{
        val token = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()
        return api.uploadTravel(
            token = token,
            id = travel.id,
            entireCountries = travel.entireCountries,
            title = travel.title,
            travelKind = travel.travelKind,
            startDate = travel.startDate!!,
            endDate = travel.endDate,
            theme = travel.theme
        ).subscribeOn(Schedulers.io())
    }

}