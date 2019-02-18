@file:Suppress("unused")

package com.kotlin.viaggio.event

import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

@Singleton
class RxEventBus {
    private val bus = PublishSubject.create<Any>()
    fun send(o: Any) { bus.onNext(o) }
    fun toObservable()= bus
}