package com.kotlin.viaggio.ioc.module.injector

import com.kotlin.viaggio.ioc.module.injector.activity.MainActivityInjectorModule
import com.kotlin.viaggio.ioc.module.provider.AppProviderModule
import com.kotlin.viaggio.view.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(
    includes = [
        AppProviderModule::class
    ]
)

interface AppInjectorModule {
    @ContributesAndroidInjector(modules = [
        MainActivityInjectorModule::class
    ])
    fun mainActivity(): MainActivity
}