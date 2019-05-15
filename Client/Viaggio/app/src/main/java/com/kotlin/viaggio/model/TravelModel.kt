package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor() : BaseModel() {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var api: ViaggioApiService

    private fun getToken() = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID)
    fun uploadTravel(travel: Travel): Single<Response<Any>> {
        val token = getToken().blockingGet()
        return api.uploadTravel(
            id = travel.id,
            area = travel.area,
            title = travel.title,
            travelKind = travel.travelKind,
            startDate = travel.startDate!!,
            endDate = travel.endDate,
            theme = travel.theme
        ).subscribeOn(Schedulers.io())
    }

    fun updateTravel(travel: Travel) :Single<Response<Any>> {
        val token = getToken().blockingGet()
        return api.updateTravel(
            travelId = travel.id,
            token = token
        )
    }

    fun deleteTravel(travelId: Long): Single<Response<Any>> {
        val token = getToken().blockingGet()
        return api.deleteTravel(travelId, token, true)
    }

    fun uploadTravelCard(travelCard: TravelCard):Single<Response<Any>> {
        val token = getToken().blockingGet()
        return api.uploadTravelCard(
            travelId = travelCard.travelId,
            token = token,
            travelOfDay = travelCard.travelOfDay,
            country = travelCard.country,
            content = travelCard.content
        )
    }

    fun updateTravelCard(travelCard: TravelCard): Single<Response<Any>> {
        val token = getToken().blockingGet()
        return api.updateTravelCard(
            travelCardId = travelCard.id,
            token = token
        )
    }

    fun deleteTravelCard(travelCardId: Long): Single<Response<Any>> {
        val token = getToken().blockingGet()
        return api.deleteTravelCard(
            travelCardId = travelCardId,
            token = token,
            isDelete = true
        )
    }

}