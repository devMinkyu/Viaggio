package com.kotlin.viaggio.model

import android.graphics.Bitmap
import com.kotlin.viaggio.data.`object`.Travel
import io.fotoapparat.result.PhotoResult
import io.reactivex.Completable
import io.reactivex.CompletableOnSubscribe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor():BaseModel(){
    fun savePicture(photoResult: PhotoResult)=
        localDataSource.savePhotoResult(photoResult)
    fun imageAllPath() =
        localDataSource.imageAllPath()
    fun cacheImage(bitmap:Bitmap) =
            localDataSource.cacheFile(bitmap)

    fun createTravel(travel:Travel):Completable{
        return Completable.fromAction {
            db.get().travelDao().insertTravel(travel)
        }
    }
}