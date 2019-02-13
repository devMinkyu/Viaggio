package com.kotlin.viaggio.view

import com.kotlin.viaggio.ioc.component.DaggerAppComponent
import com.kotlin.viaggio.view.common.BaseApp

class App:BaseApp(){
    override fun applicationInjector() = DaggerAppComponent
            .builder()
            .create(this)

    override fun onCreate() {
        super.onCreate()
    }
}