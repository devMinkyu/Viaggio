package com.kotlin.viaggio.view.main_activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.extenstions.leftReplace
import com.kotlin.viaggio.extenstions.topReplace
import com.kotlin.viaggio.view.popup.BackActionDialogFragment
import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.popup.ReviewActionDialogFragment
import com.kotlin.viaggio.view.setting.*
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.sign.SignUpFragment
import com.kotlin.viaggio.view.theme.ThemeFragment
import com.kotlin.viaggio.view.travel.TravelFragment
import com.kotlin.viaggio.view.travel.calendar.TravelCalendarFragment
import com.kotlin.viaggio.view.travel.enroll.TravelEnrollFragment
import com.kotlin.viaggio.view.travel.option.TravelingInstagramShareFragment
import com.kotlin.viaggio.view.travel.option.TravelingRepresentativeImageFragment
import com.kotlin.viaggio.view.traveling.TravelingFragment
import com.kotlin.viaggio.view.traveling.country.TravelingCityFragment
import com.kotlin.viaggio.view.traveling.country.TravelingCountryFragment
import com.kotlin.viaggio.view.traveling.country.TravelingDomesticsCountryFragment
import com.kotlin.viaggio.view.traveling.day_trip.TravelDayTripFragment
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailFragment
import com.kotlin.viaggio.view.traveling.enroll.TravelingCardEnrollFragment
import com.kotlin.viaggio.view.traveling.enroll.TravelingCardImageEnrollFragment
import com.kotlin.viaggio.view.traveling.image.TravelCardImageModifyFragment
import com.kotlin.viaggio.view.tutorial.TutorialFragment
import com.kotlin.viaggio.view.tutorial.intro.IntroFragment
import org.jetbrains.anko.contentView
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar

class MainActivity : BaseActivity<MainActivityViewModel>() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
        const val REQUEST_CODE_UPDATE:Int = 1004
    }
    var settingLockActionDialogFragment: SettingLockActionDialogFragment? = null

    private var appUpdateManager:AppUpdateManager? = null
    private var listener:InstallStateUpdatedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        appUpdateCheck()
        handleIntent(intent)

        showHome()
//        if (getViewModel().checkTutorial()) {
//            showHome()
//        } else {
//            showIntro()
//        }

        getViewModel().finishActivity.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                BackActionDialogFragment().show(supportFragmentManager, BackActionDialogFragment.TAG)
            }
        })
        getViewModel().showToast.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                contentView?.snackbar(resources.getString(R.string.finish_of_msg))
            }
        })

        getViewModel().reviewRequest().observe(this, Observer {
            if(it.not()) {
                ReviewActionDialogFragment().show(supportFragmentManager, ReviewActionDialogFragment.TAG)
            }
        })
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            BackActionDialogFragment().show(supportFragmentManager, BackActionDialogFragment.TAG)
//            getViewModel().backButtonSubject.onNext(System.currentTimeMillis())
        } else {
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            when (appLinkData?.pathSegments?.get(0)) {
                "home" -> {
                    when (appLinkData.lastPathSegment) {
                        "main" -> showHome()
                        "tutorial" -> leftReplace(TutorialFragment())
                        "camera" -> topReplace(CameraFragment())
                        "theme" -> topReplace(ThemeFragment())
                        "setting" -> topReplace(SettingFragment())
                        "calendar" -> topReplace(TravelCalendarFragment())
                    }
                }
                "login" -> {
                    when (appLinkData.lastPathSegment) {
                        "normal" -> leftReplace(SignInFragment())
                        "create" -> leftReplace(SignUpFragment())
                    }
                }

                "traveling" ->
                    when (appLinkData.lastPathSegment) {
                        "days" -> leftReplace(TravelingFragment())
                        "trip" -> leftReplace(TravelDayTripFragment())
                        "start" -> leftReplace(TravelingFragment())
                        "detail" -> {
                            leftReplace(TravelingDetailFragment())
                        }
                        "theme" -> {
                        }
                        "enroll" -> leftReplace(TravelEnrollFragment())
                        "representative" -> leftReplace(TravelingRepresentativeImageFragment())
                        "image" -> topReplace(TravelingCardImageEnrollFragment())
                        "country" -> {
                            leftReplace(TravelingCountryFragment())
                        }
                        "domestic" -> leftReplace(TravelingDomesticsCountryFragment())
                        "city" -> {
                            topReplace(TravelingCityFragment())
                        }
                        "card" -> leftReplace(TravelingCardEnrollFragment())
                        "calendar" -> showTravelCalendar()
                        "modify" -> leftReplace(TravelCardImageModifyFragment())
                    }
                "setting" ->
                    when (appLinkData.lastPathSegment) {
                        "profile" -> leftReplace(SettingMyProfileFragment())
                        "password" -> leftReplace(SettingPasswordFragment())
                        "image" -> leftReplace(SettingProfileImageEnrollFragment())
                        "lock" -> leftReplace(SettingLockFragment())
                    }
                "option" ->
                    when (appLinkData.lastPathSegment) {
                        "calendar" -> showOptionCalendar()
                        "country" -> showOptionCountry()
                        "domestics" -> showOptionDomestics()
                        "theme" -> showOptionTheme()
                        "image" -> leftReplace(TravelingRepresentativeImageFragment())
                        "share" -> leftReplace(TravelingInstagramShareFragment())

                    }
                else -> {
                }
            }
        }
    }

    private fun showOptionCalendar() {
        val frag = TravelCalendarFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.MODIFY_CALENDART.name, true)
        frag.arguments = arg
        topReplace(frag)
    }

    private fun showTravelCalendar() {
        val frag = TravelCalendarFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_CALENDAR.name, true)
        frag.arguments = arg
        topReplace(frag)
    }


    private fun showOptionDomestics() {
        val frag = TravelingDomesticsCountryFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_OPTION.name, true)
        frag.arguments = arg
        leftReplace(frag)
    }
    private fun showOptionCountry() {
        val frag = TravelingCountryFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_OPTION.name, true)
        frag.arguments = arg
        leftReplace(frag)
    }

    private fun showOptionTheme() {
        val frag = ThemeFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_OPTION.name, true)
        frag.arguments = arg
        leftReplace(frag)
    }

    private fun showIntro() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.show, 0)
            .replace(R.id.content_frame, IntroFragment(), null)
            .commit()
    }
    private fun showHome() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.show, 0)
            .replace(R.id.content_frame, TravelFragment(), null)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        if(getViewModel().getLock()){
            val settingLockActionDialogFragment1Val = settingLockActionDialogFragment?.run {
                return
            }?:supportFragmentManager.findFragmentByTag(SettingLockActionDialogFragment.TAG)
            val settingLockActionDialogFragmentVal = settingLockActionDialogFragment1Val?.run {
                return
            }?:SettingLockActionDialogFragment()
            settingLockActionDialogFragment = settingLockActionDialogFragmentVal
            settingLockActionDialogFragmentVal.show(supportFragmentManager, SettingLockActionDialogFragment.TAG)
        }
    }

    private fun appUpdateCheck() {
        appUpdateManager?.let {
            it.appUpdateInfo.addOnSuccessListener {appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    it.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE, // or AppUpdateType.FLEXIBLE
                        this,
                        REQUEST_CODE_UPDATE
                    )
                }
            }
        }

        listener = InstallStateUpdatedListener {
            // Handle install state
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate()
            }
        }
        appUpdateManager?.registerListener(listener)
    }
    private fun popupSnackBarForCompleteUpdate() {
        contentView?.longSnackbar(getString(R.string.in_app_update_complete), getString(R.string.in_app_update_restart)) {
            appUpdateManager?.completeUpdate()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != Activity.RESULT_OK) {
                contentView?.snackbar(getString(R.string.in_app_update_restart))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        appUpdateManager?.unregisterListener(listener)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager?.let {
            it.appUpdateInfo
                .addOnSuccessListener {
                        appUpdateInfo ->
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackBarForCompleteUpdate()
                    }
                }
        }
    }
}
