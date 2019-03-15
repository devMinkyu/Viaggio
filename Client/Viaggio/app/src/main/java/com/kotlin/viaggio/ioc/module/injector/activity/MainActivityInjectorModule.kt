@file:Suppress("unused")

package com.kotlin.viaggio.ioc.module.injector.activity

import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.setting.SettingFragment
import com.kotlin.viaggio.view.sign.SignFragment
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.sign.SignUpFragment
import com.kotlin.viaggio.view.theme.ThemeFragment
import com.kotlin.viaggio.view.theme.TravelingOfDayThemeFragment
import com.kotlin.viaggio.view.travel.TravelFragment
import com.kotlin.viaggio.view.travel.calendar.CalendarViewActionDialogFragment
import com.kotlin.viaggio.view.travel.enroll.TravelEnrollFragment
import com.kotlin.viaggio.view.travel.kinds.TravelKindsBottomSheetDialogFragment
import com.kotlin.viaggio.view.traveling.TravelingCountryFragment
import com.kotlin.viaggio.view.traveling.TravelingFinishActionDialogFragment
import com.kotlin.viaggio.view.traveling.TravelingFragment
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailActionDialogFragment
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailFragment
import com.kotlin.viaggio.view.traveling.detail.TravelingRepresentativeImageFragment
import com.kotlin.viaggio.view.traveling.traveling_card.TravelingCardEnrollFragment
import com.kotlin.viaggio.view.traveling.traveling_card.TravelingTransportationBottomSheetDialogFragment
import com.kotlin.viaggio.view.tutorial.TutorialFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainActivityInjectorModule {
    @ContributesAndroidInjector
    fun tutorialFragment(): TutorialFragment

    @ContributesAndroidInjector
    fun travelingFragment(): TravelingFragment

    @ContributesAndroidInjector
    fun traveledFragment(): TravelFragment

    @ContributesAndroidInjector
    fun settingFragment(): SettingFragment

    @ContributesAndroidInjector
    fun signFragment(): SignFragment

    @ContributesAndroidInjector
    fun signInFragment(): SignInFragment

    @ContributesAndroidInjector
    fun signUpFragment(): SignUpFragment

    @ContributesAndroidInjector
    fun cameraFragment(): CameraFragment

    @ContributesAndroidInjector
    fun themeFragment(): ThemeFragment

    @ContributesAndroidInjector
    fun travelingOfDayThemeFragment(): TravelingOfDayThemeFragment

    @ContributesAndroidInjector
    fun travelingDetailFragment(): TravelingDetailFragment

    @ContributesAndroidInjector
    fun travelingDetailActionDialogFragment(): TravelingDetailActionDialogFragment

    @ContributesAndroidInjector
    fun travelingCardEnrollFragment(): TravelingCardEnrollFragment

    @ContributesAndroidInjector
    fun travelingPlaceBottomSheetDialogFragment(): TravelingTransportationBottomSheetDialogFragment

    @ContributesAndroidInjector
    fun travelingRepresentativeImageFragment(): TravelingRepresentativeImageFragment

    @ContributesAndroidInjector
    fun travelingFinishActionDialogFragment(): TravelingFinishActionDialogFragment

    @ContributesAndroidInjector
    fun travelingCountryFragment(): TravelingCountryFragment

    @ContributesAndroidInjector
    fun travelEnrollFragment(): TravelEnrollFragment

    @ContributesAndroidInjector
    fun travelKindsBottomSheetDialogFragment(): TravelKindsBottomSheetDialogFragment

    @ContributesAndroidInjector
    fun calendarViewActionDialogFragment(): CalendarViewActionDialogFragment
}