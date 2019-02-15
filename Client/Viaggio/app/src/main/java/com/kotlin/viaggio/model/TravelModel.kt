package com.kotlin.viaggio.model

import android.net.Uri
import io.fotoapparat.result.PhotoResult
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor():BaseModel(){
    fun savePicture(photoResult: PhotoResult): Single<Uri> {
        return localDataSource.savePhotoResult(photoResult)
    }
}