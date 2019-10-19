package com.kotlin.viaggio.view

import com.crashlytics.android.Crashlytics
import com.kotlin.viaggio.ioc.component.DaggerAppComponent
import com.kotlin.viaggio.view.common.BaseApp
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import com.crashlytics.android.answers.Answers



class App : BaseApp() {
    override fun applicationInjector() = DaggerAppComponent
        .factory()
        .create(this)

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Fabric.with(this, Answers())
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement) =
                super.createStackElementTag(element) + " : " + element.lineNumber

        })
    }
}