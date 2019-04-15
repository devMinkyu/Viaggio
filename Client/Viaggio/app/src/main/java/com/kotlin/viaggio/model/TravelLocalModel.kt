package com.kotlin.viaggio.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.paging.DataSource
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
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

    private fun getTravelingId() = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID)
    private fun getTravelingOfDayId() = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ID)
    private fun getSelectedTravelingId() = prefUtilService.getLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID)
    private fun getSelectedTravelingOfDayId() =
        prefUtilService.getLong(AndroidPrefUtilService.Key.SELECTED_TRAVELING_OF_DAY_ID)

    fun savePicture(photoResult: PhotoResult) =
        localDataSource.savePhotoResult(photoResult)

    fun imageAllPath() =
        localDataSource.imageAllPath()

    fun createTravel(travel: Travel): Single<Long> {
        val bitmap = rxEventBus.travelOfFirstImage.value
        return if (bitmap == null) {
            db.get().travelDao().insertTravel(travel).subscribeOn(Schedulers.io())
        } else {
            Single.create(SingleOnSubscribe<Bitmap> {
                rxEventBus.travelOfFirstImage.subscribe { t ->
                    it.onSuccess(t)
                }
            }).flatMap { t ->
                localDataSource.cacheFile(t)
                    .flatMap { uri ->
                        travel.backgroundImageName = Uri.parse(uri[0]).lastPathSegment!!
                        db.get().travelDao().insertTravel(travel).subscribeOn(Schedulers.io())
                    }
            }.subscribeOn(Schedulers.io())
        }
    }

    fun getTravel(): Single<Travel> {
        return db.get().travelDao().getTravel(getTravelingId().blockingGet()).subscribeOn(Schedulers.io())
    }

    fun getTravels(): Single<List<Travel>> {
        return db.get().travelDao().getTravels()
    }

    fun updateTravel(travel: Travel):Completable {
        return Completable.fromAction {
            db.get().travelDao().updateTravel(travel)
        }.subscribeOn(Schedulers.io())
    }

    fun createTravelCard(travelCard: TravelCard): Completable {
        return Completable.create {
            db.get().travelDao().insertTravelCard(travelCard)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
    }

    fun updateTravelCard(travelCard: TravelCard){
        Completable.create {
            db.get().travelDao().updateTravelCard(travelCard)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun imagePathList(imageChooseList: List<Bitmap>): Single<List<String>> {
        return localDataSource.cacheFile(imageChooseList)
    }

    fun createTravelOfDay(travelOfDay: TravelOfDay): Single<Long> {
        return db.get().travelDao().insertTravelOfDay(travelOfDay).subscribeOn(Schedulers.io())
    }

    fun createTravelOfDays(travelOfDay: MutableList<TravelOfDay>): Single<MutableList<Long>> {
        return db.get().travelDao().insertAllTravelOfDay(*travelOfDay.toTypedArray()).subscribeOn(Schedulers.io())
    }

    fun getTravelOfDays(): DataSource.Factory<Int, TravelOfDay> {
        val travelingId = getTravelingId().blockingGet()
        val selectedTravelingId = getSelectedTravelingId().blockingGet()
        return if (travelingId != selectedTravelingId) {
            db.get().travelDao().getTravelOfDays(selectedTravelingId)
        } else {
            db.get().travelDao().getTravelingOfDays(selectedTravelingId)
        }
    }

    fun getTravelOfDay(): Single<TravelOfDay> {
        return db.get().travelDao().getTravelOfDay(getSelectedTravelingOfDayId().blockingGet())
            .subscribeOn(Schedulers.io())
    }

    fun getTravelOfDayCount(day: Int): Single<TravelOfDay> {
        return db.get().travelDao().getTravelOfDayCount(day, getTravelingId().blockingGet())
    }

    fun updateTravelOfDay(travelOfDay: TravelOfDay):Completable {
        return Completable.create {
            db.get().travelDao().updateTravelOfDay(travelOfDay)
            it.onComplete()
        }
            .subscribeOn(Schedulers.io())
    }

    fun getTravelCards(): Single<MutableList<TravelCard>> {
        return db.get().travelDao().getTravelCards()
            .subscribeOn(Schedulers.io())
    }
    fun getTravelCard(): Single<TravelCard> {
        return db.get().travelDao().getTravelCard(getSelectedTravelingOfDayId().blockingGet())
            .subscribeOn(Schedulers.io())
    }
}