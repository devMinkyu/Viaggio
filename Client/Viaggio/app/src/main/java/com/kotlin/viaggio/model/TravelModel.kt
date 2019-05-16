package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.obj.ViaggioTravelResult
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor() : BaseModel() {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var api: ViaggioApiService

    fun uploadTravel(travel: Travel): Single<Response<ViaggioTravelResult>> {
        val dateFormat = SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.ENGLISH)
        val dateFormated = dateFormat.format(travel.startDate)
        val endDateFormated = travel.endDate?.let {
            dateFormat.format(travel.endDate)
        }
        return api.uploadTravel(
            localId = travel.localId,
            title = travel.title,
            area = travel.area,
            travelKind = travel.travelKind,
            startDate = dateFormated,
            endDate = endDateFormated,
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
        val dateFormat = SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.ENGLISH)
        val dateFormated = dateFormat.format(travelCard.date)
        return api.uploadTravelCard(
            travelServerId = travelCard.travelServerId,
            travelOfDay = travelCard.travelOfDay,
            country = travelCard.country,
            content = travelCard.content,
            date = dateFormated,
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