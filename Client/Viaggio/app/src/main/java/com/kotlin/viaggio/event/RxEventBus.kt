@file:Suppress("unused")

package com.kotlin.viaggio.event

import android.graphics.Bitmap
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RxEventBus @Inject constructor() {
    val travelOfCountry:BehaviorSubject<String> = BehaviorSubject.create()
    val travelOfGo:BehaviorSubject<Any> = BehaviorSubject.create()
    val travelOfFirstImage: BehaviorSubject<Bitmap> = BehaviorSubject.create()
    val travelOfTheme: BehaviorSubject<List<String>> = BehaviorSubject.create()
}