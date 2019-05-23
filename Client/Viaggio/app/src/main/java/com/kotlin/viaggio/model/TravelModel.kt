package com.kotlin.viaggio.model

import com.google.gson.Gson
import com.kotlin.viaggio.data.obj.*
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Flowable
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
    @Inject
    lateinit var gson: Gson

    val dateFormat = SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.ENGLISH)
    fun uploadTravel(travel: Travel): Single<Response<ViaggioApiTravelResult>> {
        val dateFormated = dateFormat.format(travel.startDate)
        val endDateFormated = travel.endDate?.let {
            dateFormat.format(travel.endDate)
        }
        return api.uploadTravel(
            localId = travel.localId,
            title = travel.title,
            area = gson.toJson(travel.area),
            travelKind = travel.travelKind,
            startDate = dateFormated,
            endDate = endDateFormated,
            theme = gson.toJson(travel.theme)
        ).subscribeOn(Schedulers.io())
    }

    fun updateTravel(travel: Travel) :Single<Response<Any>> {
        val endDateFormated = travel.endDate?.let {
            dateFormat.format(travel.endDate)
        }
        return api.updateTravel(
            serverId = travel.serverId,
            area = gson.toJson(travel.area),
            theme = gson.toJson(travel.theme),
            imageName = travel.imageName,
            imageUrl = travel.imageUrl,
            title = travel.title,
            share = travel.share,
            endDate = endDateFormated
        ).subscribeOn(Schedulers.io())
    }
    fun deleteTravel(travelId: Int) =
        api.deleteTravel(travelId).subscribeOn(Schedulers.io())
    fun getTravels() =
            api.getTravels().subscribeOn(Schedulers.io())


    fun uploadTravelCard(travelCard: TravelCard):Single<Response<ViaggioApiTravelResult>> {
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
            imageName = travelCard.imageNames
        ).subscribeOn(Schedulers.io())
    }

    fun updateTravelCard(travelCard: TravelCard) =
        api.updateTravelCard(
            serverId = travelCard.serverId,
            content = travelCard.content
        ).subscribeOn(Schedulers.io())
    fun deleteTravelCard(travelCardId: Int) =
        api.deleteTravelCard(
            serverId = travelCardId
        ).subscribeOn(Schedulers.io())
    fun getTravelCards() =
        api.getTravelCards().subscribeOn(Schedulers.io())

    fun sync() =
        api.sycnCheckCount().subscribeOn(Schedulers.io())



}