package com.kotlin.viaggio.worker

import android.content.Context
import android.text.TextUtils
import androidx.work.WorkerParameters
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.google.gson.Gson
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.model.UserModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UploadTravelWorker @Inject constructor(context: Context, params: WorkerParameters) : BaseWorker(context, params) {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var transferUtility: TransferUtility
    @Inject
    lateinit var config: DeveloperAuthenticationProvider
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var userModel: UserModel

    override fun doWork(): Result {
        super.doWork()
        val toJson = inputData.getString(WorkerName.TRAVEL.name) ?: ""
        var travel = gson.fromJson(toJson, Travel::class.java) ?: Travel()

        val toJson1 = inputData.getString(WorkerName.TRAVEL_CARD.name) ?: ""
        val travelCard = gson.fromJson(toJson1, TravelCard::class.java) ?: TravelCard()

        if (travel.localId != 0L) {
            travelLocalModel.getTravel(travelId = travel.localId)
                .flatMap { mTravel ->
                    travel = mTravel
                    travelModel.uploadTravel(travel)
                }.flatMapCompletable {
                    if (it.isSuccessful) {
                        travel.userExist = true
                        travel.serverId = it.body()?.id ?: 0
                        travelLocalModel.updateTravel(travel)
                    } else {
                        Completable.complete()
                    }
                }.blockingAwait()
        }

        if (travelCard.localId != 0L) {
            travelLocalModel.getTravelCard(travelCard.localId)
                .flatMapCompletable {travelCards ->
                    if(travelCards.isNotEmpty()) {
                        uploadTravelCard(travelCards.first())
                    } else {
                        uploadTravelCard(travelCard)
                    }
                }.blockingAwait()
        }
        return Result.success()
    }


    private fun uploadTravelCard(travelCard: TravelCard):Completable {
        return if (travelCard.imageNames.isNotEmpty()) {
            userModel.getAws()
                .flatMapCompletable { response ->
                    if(response.isSuccessful) {
                        val list = travelCard.imageNames.map { imageName ->
                            userModel.putAwsImage(imageName)
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
                    } else {
                        Completable.complete()
                    }
                }
        } else {
            travelModel.uploadTravelCard(travelCard)
                .flatMapCompletable {
                    if (it.isSuccessful) {
                        travelCard.userExist = true
                        travelCard.serverId = it.body()?.id ?: 0
                        travelLocalModel.updateTravelCard(travelCard)
                    } else {
                        Completable.complete()
                    }
                }
        }
    }
}