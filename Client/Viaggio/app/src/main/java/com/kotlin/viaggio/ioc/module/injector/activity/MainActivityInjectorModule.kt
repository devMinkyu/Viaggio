@file:Suppress("unused")

package com.kotlin.viaggio.ioc.module.injector.activity

import com.kotlin.viaggio.ioc.module.injector.activity.fragment.HomeFragmentInjectorModule
import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.home.HomeFragment
import com.kotlin.viaggio.view.ocr.OcrImageFragment
import com.kotlin.viaggio.view.sign.SignFragment
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.sign.SignUpFragment
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
    fun ocrImageFragment(): OcrImageFragment

}