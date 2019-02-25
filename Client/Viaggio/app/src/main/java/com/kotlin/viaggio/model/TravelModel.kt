package com.kotlin.viaggio.model

import android.graphics.Bitmap
import android.net.Uri
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.event.RxEventBus
import io.fotoapparat.result.PhotoResult
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor() : BaseModel() {
    @Inject
    lateinit var rxEventBus: RxEventBus

    fun savePicture(photoResult: PhotoResult) =
        localDataSource.savePhotoResult(photoResult)

    fun imageAllPath() =
        localDataSource.imageAllPath()

    fun createTravel(travel: Travel): Single<Long> {
        return Single.create( SingleOnSubscribe<Bitmap>{
            rxEventBus.travelOfFirstImage
                .subscribe { t ->
                    it.onSuccess(t)
                }
        }).flatMap {
            localDataSource.cacheFile(it)
                .flatMap { uri ->
                    travel.themeImageName = Uri.parse(uri[0]).lastPathSegment!!
                    db.get().travelDao().insertTravel(travel)
                }
        }
    }
}