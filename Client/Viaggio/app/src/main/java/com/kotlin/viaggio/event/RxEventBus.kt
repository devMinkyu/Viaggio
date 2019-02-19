@file:Suppress("unused")

package com.kotlin.viaggio.event

import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RxEventBus @Inject constructor() {
    val bus:BehaviorSubject<Any> = BehaviorSubject.create()
}