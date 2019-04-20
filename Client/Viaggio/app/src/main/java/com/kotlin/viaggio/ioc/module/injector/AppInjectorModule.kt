package com.kotlin.viaggio.ioc.module.injector

import com.kotlin.viaggio.ioc.module.injector.activity.MainActivityInjectorModule
import com.kotlin.viaggio.ioc.module.provider.AppProviderModule
import com.kotlin.viaggio.view.main_activity.MainActivity
import com.kotlin.viaggio.worker.TimeCheckWorker
import com.kotlin.viaggio.worker.UploadTravelWorker
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(
    includes = [
        AppProviderModule::class
    ]
)
@Suppress("unused")
interface AppInjectorModule {
    @ContributesAndroidInjector(
        modules = [
            MainActivityInjectorModule::class
        ]
    )
    fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    fun timeCheckWorker():TimeCheckWorker

    @ContributesAndroidInjector
    fun uploadTravelWorker():UploadTravelWorker
}