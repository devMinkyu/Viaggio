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
    private fun travelOfTravelBodyConvert(travel: Travel):TravelBody {
        val dateFormated = dateFormat.format(travel.startDate)
        val endDateFormated = travel.endDate?.let {
            dateFormat.format(travel.endDate)
        }
        return TravelBody().apply {
            localId = travel.localId
            startDate = dateFormated
            endDate = endDateFormated
            area = travel.area
            theme = travel.theme
            imageName = travel.imageName
            imageUrl = travel.imageUrl
            isDelete = travel.isDelete
            serverId = travel.serverId
            share = travel.share
            title = travel.title
            travelKind = travel.travelKind
        }
    }
    fun uploadTravel(travel: Travel): Single<Response<ViaggioApiTravelResult>> {
        return api.uploadTravel(
            travel = travelOfTravelBodyConvert(travel)
        ).subscribeOn(Schedulers.io())
    }

    fun updateTravel(travel: Travel) :Single<Response<Any>> {
        return api.updateTravel(
            serverId = travel.serverId,
            travel = travelOfTravelBodyConvert(travel)
        ).subscribeOn(Schedulers.io())
    }
    fun deleteTravel(travelId: Int) =
        api.deleteTravel(travelId).subscribeOn(Schedulers.io())
    fun getTravels() =
            api.getTravels().subscribeOn(Schedulers.io())

    private fun travelCardOfTravelCardBodyConvert(travelCard: TravelCard):TravelCardBody {
        val dateFormated = dateFormat.format(travelCard.date)
        return TravelCardBody().apply {
            localId = travelCard.localId
            serverId = travelCard.serverId
            travelLocalId = travelCard.travelLocalId
            travelServerId = travelCard.travelServerId
            travelOfDay = travelCard.travelOfDay
            country = travelCard.country
            theme = travelCard.theme
            imageNames = travelCard.imageNames
            imageUrl = travelCard.imageUrl
            content = travelCard.content
            date = dateFormated
            isDelete = travelCard.isDelete
        }
    }
    fun uploadTravelCard(travelCard: TravelCard):Single<Response<ViaggioApiTravelResult>> {
        return api.uploadTravelCard(
            travelServerId = travelCard.travelServerId,
            travelCardBody = travelCardOfTravelCardBodyConvert(travelCard)
        ).subscribeOn(Schedulers.io())
    }

    fun updateTravelCard(travelCard: TravelCard) =
        api.updateTravelCard(
            serverId = travelCard.serverId,
            travelCardBody = travelCardOfTravelCardBodyConvert(travelCard)
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