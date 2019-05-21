package com.kotlin.viaggio.ioc.component

import com.kotlin.viaggio.ioc.module.common.AndroidXInjectionModule
import com.kotlin.viaggio.ioc.module.injector.AppInjectorModule
import com.kotlin.viaggio.view.App
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidXInjectionModule::class,
        AppInjectorModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Factory
    interface Builder : AndroidInjector.Factory<App>
}