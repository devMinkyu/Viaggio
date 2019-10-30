package com.kotlin.viaggio.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.paging.DataSource
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.RxEventBus
import io.fotoapparat.result.PhotoResult
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelLocalModel @Inject constructor() : BaseModel() {
    @Inject
    lateinit var rxEventBus: RxEventBus
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var transferUtility: TransferUtility
    @Inject
    lateinit var config: DeveloperAuthenticationProvider

    fun getTravelingId() = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID)
    fun getSelectedTravelingId() = prefUtilService.getLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID)
    private fun getSelectedTravelingCardId() =
        prefUtilService.getLong(AndroidPrefUtilService.Key.SELECTED_TRAVELING_CARD_ID)

    fun getToken():String = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()
    fun getUploadMode():Int = prefUtilService.getInt(AndroidPrefUtilService.Key.UPLOAD_MODE).blockingGet()

    fun savePicture(photoResult: PhotoResult) =
        localDataSource.savePhotoResult(photoResult)

    fun imageAllPath() = localDataSource.imageAllPath()
    fun folderName() = localDataSource.folderName()

    fun imageOfFolder(folder:String) = localDataSource.imageOfFolder(folder)

    fun createTravel(travel: Travel): Completable {
        val bitmap = rxEventBus.travelOfFirstImage.value
        return if (bitmap == null) {
            Completable.fromAction {
                db.get().travelDao().insertTravel(travel)
            }
                .subscribeOn(Schedulers.io())
        } else {
            Single.create(SingleOnSubscribe<Bitmap> {
                rxEventBus.travelOfFirstImage.subscribe { t ->
                    it.onSuccess(t)
                }
            }).flatMapCompletable { t ->
                localDataSource.cacheFile(t)
                    .flatMapCompletable { uri ->
                        travel.imageName = Uri.parse(uri[0]).lastPathSegment!!
                        Completable.fromAction {
                            db.get().travelDao().insertTravel(travel)
                        }.subscribeOn(Schedulers.io())
                    }
            }.subscribeOn(Schedulers.io())
        }
    }
    fun createTravels(vararg travel: Travel): Completable {
        return Completable.fromAction {
            db.get().travelDao().insertTravel(*travel)
        }.subscribeOn(Schedulers.io())
    }

    fun getTravel(): Single<Travel> {
        return db.get().travelDao().getTravel(getSelectedTravelingId().blockingGet()).subscribeOn(Schedulers.io())
    }
    fun getTravel(travelId:Long): Single<Travel> {
        return db.get().travelDao().getTravel(travelId)
    }

    fun getTravels(): Single<List<Travel>> {
        return db.get().travelDao().getTravels()
    }
    fun getNotUploadTravels(): Single<List<Travel>> {
        return db.get().travelDao().getNotUploadTravels()
    }

    fun updateTravel(travel: Travel):Completable {
        return Completable.fromAction {
            db.get().travelDao().updateTravel(travel)
        }.subscribeOn(Schedulers.io())
    }
    fun updateTravel(vararg travel: Travel):Completable {
        return Completable.fromAction {
            db.get().travelDao().updateTravel(*travel)
        }.subscribeOn(Schedulers.io())
    }

    fun createTravelCard(vararg travelCard: TravelCard): Completable {
        return Completable.fromAction {
            db.get().travelDao().insertTravelCard(*travelCard)
        }
            .subscribeOn(Schedulers.io())
    }

    fun updateTravelCard(travelCard: TravelCard):Completable{
        return Completable.fromAction {
            db.get().travelDao().updateTravelCard(travelCard)
        }.subscribeOn(Schedulers.io())
    }
    fun updateTravelCards(travelCards: List<TravelCard>):Completable{
        return Completable.fromAction {
            db.get().travelDao().updateTravelCard(*travelCards.toTypedArray())
        }.subscribeOn(Schedulers.io())
    }

    fun imagePathList(imageChooseList: List<Bitmap>): Single<List<String>> {
        return localDataSource.cacheFile(imageChooseList)
    }
    fun getTravelDayTripPaging(): DataSource.Factory<Int, TravelCard> {
        val selectedTravelingId = getSelectedTravelingId().blockingGet()
        return db.get().travelDao().getTravelDayTripAsc(selectedTravelingId)
    }
    fun getTravelCardsPaging(): DataSource.Factory<Int, TravelCard> {
        val travelingId = getTravelingId().blockingGet()
        val selectedTravelingId = getSelectedTravelingId().blockingGet()
        return if (travelingId != selectedTravelingId) {
            db.get().travelDao().getTravelCardAsc(selectedTravelingId)
        } else {
            db.get().travelDao().getTravelCardDes(selectedTravelingId)
        }
    }
    fun getTravelCards(): Single<MutableList<TravelCard>> {
        return db.get().travelDao().getTravelCards(getSelectedTravelingId().blockingGet())
            .subscribeOn(Schedulers.io())
    }

    fun getNotUploadTravelCards(): Single<MutableList<TravelCard>> {
        return db.get().travelDao().getNotUploadTravelCards()
            .subscribeOn(Schedulers.io())
    }
    fun getTravelCard(): Single<List<TravelCard>> {
        return db.get().travelDao().getTravelCard(getSelectedTravelingCardId().blockingGet())
            .subscribeOn(Schedulers.io())
    }
    fun getTravelCard(travelCardId:Long): Single<List<TravelCard>> {
        return db.get().travelDao().getTravelCard(travelCardId)
            .subscribeOn(Schedulers.io())
    }

    fun clearTravel() =
        Completable.fromAction {
            db.get().clearAllTables()
        }.subscribeOn(Schedulers.io())
}