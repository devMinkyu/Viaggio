@file:Suppress("unused")

package com.kotlin.viaggio.event

import android.graphics.Bitmap
import com.kotlin.viaggio.data.`object`.TravelOfDay
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
    val travelOfDayTheme: BehaviorSubject<List<String>> = BehaviorSubject.create()
    val travelOfDayChange: BehaviorSubject<Boolean> = BehaviorSubject.create()
    val travelCardTransportation: BehaviorSubject<String> = BehaviorSubject.create()
}