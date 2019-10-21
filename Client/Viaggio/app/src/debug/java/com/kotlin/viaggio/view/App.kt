package com.kotlin.viaggio.view

import com.facebook.stetho.Stetho
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.kotlin.viaggio.ioc.component.DaggerAppComponent
import com.kotlin.viaggio.view.common.BaseApp
import timber.log.Timber


class App : BaseApp() {
    override fun applicationInjector() = DaggerAppComponent
        .factory()
        .create(this)

    override fun onCreate() {
        super.onCreate()

        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build()
        )

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement) =
                super.createStackElementTag(element) + " : " + element.lineNumber

        })
        MobileAds.initialize(applicationContext, OnInitializationCompleteListener {

        })
    }
}