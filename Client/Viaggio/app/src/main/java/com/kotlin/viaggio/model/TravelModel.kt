package com.kotlin.viaggio.model

import android.accounts.NetworkErrorException
import com.google.gson.Gson
import com.kotlin.viaggio.data.obj.*
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Completable
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
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private fun travelOfTravelBodyConvert(travel: Travel): TravelBody {
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
    fun createSyncTravels(travels: List<Travel>): Single<Response<ViaggioApiTravelsResult>> {
        val result = travels.map {
            travelOfTravelBodyConvert(it)
        }
        val data = TravelBodyList(result)
        return api.createTravels(data).subscribeOn(Schedulers.io())
    }

    fun updateSyncTravels(travels: List<Travel>): Completable {
        val result = travels.map {
            travelOfTravelBodyConvert(it)
        }
        val data = TravelBodyList(result)
        return api.updateTravels(data).subscribeOn(Schedulers.io())
            .flatMapCompletable {
                if(it.isSuccessful) {
                    val list = travels.map {travel ->
                        travel.userExist = true
                        travel
                    }
                    travelLocalModel.updateTravel(*list.toTypedArray())
                } else {
                    Completable.error(NetworkErrorException("Data sync error"))
                }
            }
    }

    fun uploadTravel(travel: Travel): Single<Response<ViaggioApiTravelResult>> {
        return api.uploadTravel(
            travel = travelOfTravelBodyConvert(travel)
        ).subscribeOn(Schedulers.io())
    }

    fun updateTravel(travel: Travel): Single<Response<Any>> {
        return api.updateTravel(
            serverId = travel.serverId,
            travel = travelOfTravelBodyConvert(travel)
        ).subscribeOn(Schedulers.io())
    }

    fun deleteTravel(travelId: Int) =
        api.deleteTravel(travelId).subscribeOn(Schedulers.io())

    fun getTravels(): Single<List<TravelBody>> {
        return api.getTravels().subscribeOn(Schedulers.io())
            .flatMap {
                if (it.isSuccessful) {
                    Single.just(it.body()!!.travels)
                } else {
                    Single.just(listOf())
                }
            }
    }


    private fun travelCardOfTravelCardBodyConvert(travelCard: TravelCard): TravelCardBody {
        val dateFormated = dateFormat.format(travelCard.date)
        val timeFormated = dateFormat.format(travelCard.time)
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
            time = timeFormated
            isDelete = travelCard.isDelete

        }
    }

    fun uploadTravelCard(travelCard: TravelCard): Single<Response<ViaggioApiTravelResult>> {
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

    fun getTravelCards(): Single<List<TravelCardBody>> {
        return api.getTravelCards().subscribeOn(Schedulers.io())
            .flatMap {
                if(it.isSuccessful) {
                    Single.just(it.body()!!.travelCards)
                } else{
                    Single.just(listOf())
                }
            }

    }

    fun updateTravelCards(travelCards: List<TravelCard>): Completable {
        val result = travelCards.map {
            travelCardOfTravelCardBodyConvert(it)
        }
        val data = TravelCardBodyList(result)
        return api.updateTravelCards(data).subscribeOn(Schedulers.io())
            .flatMapCompletable {
                if(it.isSuccessful) {
                    val list = travelCards.map {travelCard ->
                        travelCard.userExist = true
                        travelCard
                    }
                    travelLocalModel.updateTravelCards(list)
                } else {
                    Completable.error(NetworkErrorException("Data sync error"))
                }
            }
    }


    fun createSyncTravelCards(travelCards: List<TravelCard>): Completable {
        val result = travelCards.map {
            travelCardOfTravelCardBodyConvert(it)
        }
        val data = TravelCardBodyList(result)
        return api.createTravelCards(data).subscribeOn(Schedulers.io())
            .flatMapCompletable {
                if(it.isSuccessful) {
                    val travelCardList = it.body()!!.travelCards.mapNotNull {
                        travelCards.firstOrNull { travelCard ->
                            travelCard.localId == it.localId
                        }?.let { travelCard ->
                            travelCard.userExist = true
                            travelCard.localId = it.localId
                            travelCard.serverId = it.serverId
                            travelCard.travelLocalId = it.travelLocalId
                            travelCard.travelServerId = it.travelServerId
                            travelCard
                        }
                    }
                    travelLocalModel.updateTravelCards(travelCardList)
                } else {
                    Completable.error(NetworkErrorException("Data sync error"))
                }
            }
    }
    fun updateSyncTravelCards(travelCards: List<TravelCard>): Completable {
        val result = travelCards.map {
            travelCardOfTravelCardBodyConvert(it)
        }
        val data = TravelCardBodyList(result)
        return api.updateTravelCards(data).subscribeOn(Schedulers.io())
            .flatMapCompletable {
                if(it.isSuccessful) {
                    val list = travelCards.map { travelCard ->
                        travelCard.userExist = true
                        travelCard
                    }
                    travelLocalModel.updateTravelCards(list)
                } else {
                    Completable.error(NetworkErrorException("Data sync error"))
                }
            }
    }

    fun sync() =
        api.syncCheckCount().subscribeOn(Schedulers.io())


}