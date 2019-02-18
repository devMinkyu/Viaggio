@file:Suppress("unused")

package com.kotlin.viaggio.ioc.module.injector.activity.fragment

import com.kotlin.viaggio.view.home.HomeMainFragment
import com.kotlin.viaggio.view.traveled.TraveledFragment
import com.kotlin.viaggio.view.traveling.TravelingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface HomeFragmentInjectorModule {
    @ContributesAndroidInjector
    fun homeMainFragment(): HomeMainFragment

    @ContributesAndroidInjector
    fun travelingFragment(): TravelingFragment

    @ContributesAndroidInjector
    fun traveledFragment(): TraveledFragment
}