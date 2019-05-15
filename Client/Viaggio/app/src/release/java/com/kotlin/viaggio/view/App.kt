package com.kotlin.viaggio.view

import com.kotlin.viaggio.ioc.component.DaggerAppComponent
import com.kotlin.viaggio.view.common.BaseApp
import timber.log.Timber

class App : BaseApp() {
    override fun applicationInjector() = DaggerAppComponent
        .factory()
        .create(this)

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement) =
                super.createStackElementTag(element) + " : " + element.lineNumber

        })
    }
}