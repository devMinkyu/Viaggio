package com.kotlin.viaggio.worker

import android.content.Context
import android.text.TextUtils
import androidx.work.WorkerParameters
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.model.UserModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class SynchronizeDataFetchWorker @Inject constructor(context: Context, params: WorkerParameters) : BaseWorker(context, params) {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var transferUtility: TransferUtility
    @Inject
    lateinit var config: DeveloperAuthenticationProvider
    @Inject
    lateinit var userModel: UserModel
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService

    override fun doWork(): Result {
        super.doWork()
        val token = travelLocalModel.getToken()
        val mode = travelLocalModel.getUploadMode()
        if (TextUtils.isEmpty(token).not() && mode != 2) {
            fetchData()
        }
        return Result.success()
    }

    fun fetchData() {
        val completables = mutableListOf<Completable>()
        val travelSingle = travelLocalModel.getNotUploadTravels()
        val travelCardSingle = travelLocalModel.getNotUploadTravelCards()
        var travelCardMap:Map<Long, List<TravelCard>> = mapOf()
        Single.zip(travelSingle, travelCardSingle, BiFunction
        <List<Travel>, MutableList<TravelCard>, List<Travel>>
        { travels, travelCards ->
            travelCardMap = travelCards.filter { it.travelServerId == 0 }.groupBy { it.travelLocalId }
            // update
            val updateTravelList = travels
                .filter { it.userExist.not() && it.serverId != 0 }
            val updateTravelCardExistImageList = travelCards
                .filter { it.userExist.not() && it.serverId != 0 && it.newImageNames.isNotEmpty()}
            val updateTravelCardNotImageList = travelCards
                .filter { it.userExist.not() && it.serverId != 0 && it.newImageNames.isEmpty()}
            if (updateTravelList.isNotEmpty()) {
                val c1 = travelModel.updateSyncTravels(updateTravelList)
                completables.add(c1)
            }
            if (updateTravelCardNotImageList.isNotEmpty()) {
                val c2 = travelModel.updateSyncTravelCards(updateTravelCardNotImageList)
                completables.add(c2)
            }
            if(updateTravelCardExistImageList.isNotEmpty()) {
                val c3 = updateTravelCard(updateTravelCardExistImageList)
                completables.add(c3)
            }

            // create
            val createTravelList = travels
                .filter { it.userExist.not() && it.serverId == 0 }
            val createTravelCardsExistImageList = travelCards
                .filter {
                    it.userExist.not() && it.serverId == 0 && it.travelServerId != 0 && it.imageNames.isNotEmpty()
                }
            val createTravelCardsNotImageList = travelCards
                .filter {
                    it.userExist.not() && it.serverId == 0 && it.travelServerId != 0 && it.imageNames.isEmpty()
                }
            if (createTravelCardsExistImageList.isNotEmpty()) {
                val c4 = createTravelCard(createTravelCardsExistImageList)
                completables.add(c4)
            }
            if(createTravelCardsNotImageList.isNotEmpty()) {
                val c3 = travelModel.createSyncTravelCards(createTravelCardsNotImageList)
                completables.add(c3)
            }
            createTravelList
        }).subscribeOn(Schedulers.io())
            .flatMapCompletable { createTravelList ->
                travelModel.createSyncTravels(createTravelList).flatMapCompletable { response ->
                    if (response.isSuccessful) {
                        // travel
                        val completables2 = mutableListOf<Completable>()
                        val travelList = response.body()!!.travels.mapNotNull {
                            val travel = createTravelList.firstOrNull { travel ->
                                travel.localId == it.localId
                            }
                            travel?.let { mTravel ->
                                mTravel.serverId = it.serverId
                                mTravel.userExist = true
                                mTravel
                            }
                        }
                        completables2.add(travelLocalModel.updateTravel(*travelList.toTypedArray()))

                        // travelCard
                        val createTravelCardList = response.body()!!.travels.mapNotNull { data ->
                            if (travelCardMap.containsKey(data.localId)) {
                                travelCardMap.getValue(data.localId).map { travelCardVal ->
                                    travelCardVal.travelServerId = data.serverId
                                    travelCardVal
                                }
                            } else {
                                null
                            }
                        }.flatten()

                        val createTravelCardExistImageList =
                            createTravelCardList.filter { it.imageNames.isNotEmpty() }
                        val createTravelCardNotImageList =
                            createTravelCardList.filter { it.imageNames.isEmpty() }

                        if (createTravelCardNotImageList.isNotEmpty()) {
                            completables2.add(travelModel.createSyncTravelCards(createTravelCardNotImageList))
                        }
                        if (createTravelCardExistImageList.isNotEmpty()) {
                            completables2.add(createTravelCard(createTravelCardExistImageList))
                        }
                        Completable.merge(completables2)
                    } else {
                        Completable.complete()
                    }
                }
            }.andThen {
                Completable.merge(completables).blockingAwait()
                it.onComplete()
            }.blockingAwait()
    }
    private fun createTravelCard(createTravelCardsExistImageList:List<TravelCard>): Completable {
        return userModel.getAws()
            .flatMapCompletable { response ->
                if(response.isSuccessful) {
                    val completableList = createTravelCardsExistImageList.map { travelCard ->
                        val list = travelCard.imageNames.map {
                            userModel.putAwsImage(it)
                        }
                        val resultList = mutableListOf<String>()
                        val newImageNames = travelCard.imageNames.map { it }
                        travelCard.newImageNames = newImageNames.toMutableList()
                        Single.merge(list)
                            .filter{
                                TextUtils.isEmpty(it).not()
                            }.map { imageUrl ->
                                val imageName = imageUrl.split("/").last()
                                val index = travelCard.newImageNames.indexOfFirst {newImageName ->
                                    newImageName.split("/").last() == imageName
                                }
                                if (index >= 0) {
                                    travelCard.newImageNames.removeAt(index)
                                }
                                resultList.add(imageUrl)
                            }.lastOrError()
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .flatMapCompletable {
                                travelCard.imageUrl = resultList
                                travelModel.uploadTravelCard(travelCard)
                                    .flatMapCompletable {
                                        if (it.isSuccessful) {
                                            travelCard.userExist = travelCard.newImageNames.isEmpty()
                                            travelCard.serverId = it.body()?.id ?: 0
                                            travelLocalModel.updateTravelCard(travelCard)
                                        } else {
                                            Completable.complete()
                                        }
                                    }
                            }
                    }
                    Completable.merge(completableList)
                } else {
                    Completable.complete()
                }
            }
    }
    private fun updateTravelCard(updateTravelCardExistImageList:List<TravelCard>): Completable {
        return userModel.getAws()
            .flatMapCompletable { response ->
                if(response.isSuccessful) {
                    val completableList = updateTravelCardExistImageList.map { travelCard ->
                        val list = travelCard.newImageNames.map {
                            userModel.putAwsImage(it)
                        }
                        val resultList = mutableListOf<String>()
                        Single.merge(list)
                            .filter {
                                TextUtils.isEmpty(it).not()
                            }.map { imageUrl ->
                                val imageName = imageUrl.split("/").last()
                                val index = travelCard.newImageNames.indexOfFirst {newImageName ->
                                    newImageName.split("/").last() == imageName
                                }
                                if (index >= 0) {
                                    travelCard.newImageNames.removeAt(index)
                                }
                                resultList.add(imageUrl)
                            }.lastOrError()
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .flatMapCompletable {
                                travelCard.imageUrl.addAll(resultList)
                                travelCard.imageUrl.distinctBy {
                                    it.split("/").last()
                                }.let { distinctList ->
                                    val finalList = travelCard.imageNames.mapNotNull {
                                        val checkImageName = it.split("/").last()
                                        if(distinctList.contains("image/${userModel.getUserId()}/$checkImageName")) {
                                            "image/${userModel.getUserId()}/$checkImageName"
                                        } else {
                                            null
                                        }
                                    }
                                    travelCard.imageUrl.clear()
                                    travelCard.imageUrl = finalList.toMutableList()
                                }
                                travelModel.updateTravelCard(travelCard)
                                    .flatMapCompletable {
                                        if (it.isSuccessful) {
                                            travelCard.userExist = travelCard.newImageNames.isEmpty()
                                            travelLocalModel.updateTravelCard(travelCard)
                                        } else {
                                            Completable.complete()
                                        }
                                    }
                            }
                    }
                    Completable.merge(completableList)
                } else {
                    Completable.complete()
                }
            }
    }
}