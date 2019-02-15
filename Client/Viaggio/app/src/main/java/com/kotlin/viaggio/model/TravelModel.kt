package com.kotlin.viaggio.model

import io.fotoapparat.result.PhotoResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor():BaseModel(){
    fun savePicture(photoResult: PhotoResult)=
        localDataSource.savePhotoResult(photoResult)


    fun imageAllPath() =
        localDataSource.imageAllPath()

}