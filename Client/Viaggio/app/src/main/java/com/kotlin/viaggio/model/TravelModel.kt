package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.obj.ViaggioTravelResult
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

    fun uploadTravel(travel: Travel): Single<Response<ViaggioTravelResult>> {
        return api.uploadTravel(
            localId = travel.localId,
            area = travel.area,
            title = travel.title,
            travelKind = travel.travelKind,
            startDate = travel.startDate!!,
            endDate = travel.endDate,
            theme = travel.theme,
            isDelete = travel.isDelete,
            share = travel.share,
            imageName = travel.imageName,
            imageUrl = travel.imageUrl
        ).subscribeOn(Schedulers.io())
    }

    fun updateTravel(travel: Travel) :Single<Response<Any>> {
        return api.updateTravel(
            serverId = travel.serverId,
            area = travel.area,
            theme = travel.theme,
            imageName = travel.imageName,
            imageUrl = travel.imageUrl,
            title = travel.title,
            share = travel.share,
            endDate = travel.endDate
        )
    }

    fun deleteTravel(travelId: Int): Single<Response<Any>> {
        return api.deleteTravel(travelId)
    }

    fun uploadTravelCard(travelCard: TravelCard):Single<Response<ViaggioTravelResult>> {
        return api.uploadTravelCard(
            travelServerId = travelCard.travelServerId,
            travelOfDay = travelCard.travelOfDay,
            country = travelCard.country,
            content = travelCard.content,
            date = travelCard.date,
            localId = travelCard.localId,
            travelLocalId = travelCard.travelLocalId,
            theme = travelCard.theme,
            imageUrl = travelCard.imageUrl,
            imageName = travelCard.imageNames,
            isDelete = travelCard.isDelete
        )
    }

    fun updateTravelCard(travelCard: TravelCard): Single<Response<Any>> {
        return api.updateTravelCard(
            serverId = travelCard.serverId,
            content = travelCard.content
        )
    }

    fun deleteTravelCard(travelCardId: Int): Single<Response<Any>> {
        return api.deleteTravelCard(
            serverId = travelCardId
        )
    }

}