@file:Suppress("unused")

package com.kotlin.viaggio.ioc.module.injector.activity

import com.kotlin.viaggio.ioc.module.injector.activity.fragment.HomeFragmentInjectorModule
import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.home.HomeFragment
import com.kotlin.viaggio.view.setting.SettingFragment
import com.kotlin.viaggio.view.sign.SignFragment
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.sign.SignUpFragment
import com.kotlin.viaggio.view.theme.ThemeFragment
import com.kotlin.viaggio.view.theme.TravelingOfDayThemeFragment
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailActionDialogFragment
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailFragment
import com.kotlin.viaggio.view.tutorial.TutorialFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainActivityInjectorModule {
    @ContributesAndroidInjector
    fun tutorialFragment(): TutorialFragment

    @ContributesAndroidInjector(
        modules = [
            HomeFragmentInjectorModule::class
        ]
    )
    fun homeFragment(): HomeFragment

    @ContributesAndroidInjector
    fun signFragment(): SignFragment

    @ContributesAndroidInjector
    fun signInFragment(): SignInFragment

    @ContributesAndroidInjector
    fun signUpFragment(): SignUpFragment

    @ContributesAndroidInjector
    fun cameraFragment(): CameraFragment

    @ContributesAndroidInjector
    fun settingFragment(): SettingFragment

    @ContributesAndroidInjector
    fun themeFragment(): ThemeFragment

    @ContributesAndroidInjector
    fun travelingOfDayThemeFragment(): TravelingOfDayThemeFragment

    @ContributesAndroidInjector
    fun travelingDetailFragment(): TravelingDetailFragment

    @ContributesAndroidInjector
    fun travelingDetailActionDialogFragment(): TravelingDetailActionDialogFragment
}