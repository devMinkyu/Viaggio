package com.kotlin.viaggio.model

import io.fotoapparat.result.PhotoResult
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelModel @Inject constructor(){
    val cameraResult:BehaviorSubject<PhotoResult> = BehaviorSubject.create()
}