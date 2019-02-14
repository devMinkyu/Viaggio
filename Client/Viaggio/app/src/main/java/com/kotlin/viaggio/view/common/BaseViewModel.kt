package com.kotlin.viaggio.view.common

import android.content.Context
import androidx.lifecycle.ViewModel
import com.tbruyelle.rxpermissions2.Permission
import dagger.Lazy
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Named

abstract class BaseViewModel:ViewModel() {
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>

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
    open fun permissionCheck(request: Observable<Permission>?) {}
}