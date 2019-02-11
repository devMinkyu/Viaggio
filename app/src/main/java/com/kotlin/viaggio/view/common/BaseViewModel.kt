package com.kotlin.viaggio.view.common

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable

abstract class BaseViewModel:ViewModel() {
    private val disposables = mutableListOf<Disposable?>()

    override fun onCleared() {
        for (disposable in disposables) {
            disposable?.dispose()
        }
        disposables.clear()
    }
    @Synchronized fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }
    open fun initialize() {}

}