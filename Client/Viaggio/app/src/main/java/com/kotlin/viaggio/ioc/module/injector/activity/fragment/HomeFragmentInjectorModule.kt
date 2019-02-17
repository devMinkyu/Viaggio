@file:Suppress("unused")

package com.kotlin.viaggio.ioc.module.injector.activity.fragment

import com.kotlin.viaggio.view.home.HomeMainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface HomeFragmentInjectorModule {
    @ContributesAndroidInjector
    fun homeMainFragment(): HomeMainFragment
}