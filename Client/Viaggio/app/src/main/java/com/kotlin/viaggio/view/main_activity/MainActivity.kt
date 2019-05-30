package com.kotlin.viaggio.view.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.github.ajalt.reprint.core.Reprint
import com.github.ajalt.reprint.rxjava2.RxReprint
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.setting.*
import com.kotlin.viaggio.view.sign.SignFragment
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.sign.SignUpFragment
import com.kotlin.viaggio.view.theme.ThemeFragment
import com.kotlin.viaggio.view.travel.TravelFragment
import com.kotlin.viaggio.view.travel.calendar.TravelCalendarFragment
import com.kotlin.viaggio.view.travel.enroll.TravelEnrollFragment
import com.kotlin.viaggio.view.travel.option.TravelingRepresentativeImageFragment
import com.kotlin.viaggio.view.traveling.TravelingFragment
import com.kotlin.viaggio.view.traveling.country.TravelingCityFragment
import com.kotlin.viaggio.view.traveling.country.TravelingCountryFragment
import com.kotlin.viaggio.view.traveling.country.TravelingDomesticsCountryFragment
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailFragment
import com.kotlin.viaggio.view.traveling.enroll.TravelingCardEnrollFragment
import com.kotlin.viaggio.view.traveling.enroll.TravelingCardImageEnrollFragment
import com.kotlin.viaggio.view.tutorial.TutorialFragment
import org.jetbrains.anko.toast

class MainActivity : BaseActivity<MainActivityViewModel>() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
    var settingLockActionDialogFragment: SettingLockActionDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showHome()
        handleIntent(intent)

//        if (getViewModel().checkTutorial()) {
//            getViewModel().initSetting()
//            showHome()
//        } else {
//            showTutorial()
//        }
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )

        getViewModel().finishActivity.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                finish()
            }
        })
        getViewModel().showToast.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                toast(resources.getString(R.string.finish_of_msg))
            }
        })
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            getViewModel().backButtonSubject.onNext(System.currentTimeMillis())
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
                        "login" -> showSign()
                        "camera" -> showCamera()
                        "theme" -> showTheme()
                        "setting" -> showSetting()
                        "calendar" -> showCalendar()
                    }
                }
                "login" -> {
                    when (appLinkData.lastPathSegment) {
                        "normal" -> showSignNormalIn()
                        "create" -> showSignCreate()
                    }
                }

                "traveling" ->
                    when (appLinkData.lastPathSegment) {
                        "days" -> showTraveling()
                        "start" -> showTraveling()
                        "detail" -> {
                            showTravelingDetail()
                        }
                        "theme" -> {
                        }
                        "enroll" -> showTravelEnroll()
                        "representative" -> showTravelingRepresentative()
                        "image" -> showTravelingEnrollImage()
                        "country" -> {
                            showTravelingCountry()
                        }
                        "domestic" -> showTravelingDomesticCountry()
                        "city" -> {
                            getViewModel().travelType = appLinkData.pathSegments[1].toInt()
                            showTravelingCity()
                        }
                        "card" -> showTravelingEnroll()
                        "calendar" -> showTravelCalendar()
                    }
                "setting" ->
                    when (appLinkData.lastPathSegment) {
                        "profile" -> showMyProfile()
                        "password" -> showChangePassword()
                        "image" -> showProfileImage()
                        "lock" -> showLock()
                    }
                "option" ->
                    when (appLinkData.lastPathSegment) {
                        "country" -> {
                            showOptionCountry()
                        }
                        "domestics" -> {
                            showOptionDomestics()
                        }
                        "theme" -> {
                            showOptionTheme()
                        }
                        "image" -> {
                            showOptionImage()
                        }
                    }
                else -> {
                }
            }
        }
    }

    private fun showLock() {
        baseShowAddLeftAddBackFragment(SettingLockFragment())
    }

    private fun showProfileImage() {
        baseShowAddLeftAddBackFragment(SettingProfileImageEnrollFragment())
    }

    private fun showTravelCalendar() {
        val frag = TravelCalendarFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_CALENDAR.name, true)
        frag.arguments = arg
        baseShowBottomAddBackFragment(frag)
    }


    private fun showCalendar() {
        baseShowBottomAddBackFragment(TravelCalendarFragment())
    }

    private fun showTravelingDomesticCountry() {
        baseShowAddLeftAddBackFragment(TravelingDomesticsCountryFragment())
    }

    private fun showChangePassword() {
        baseShowAddLeftAddBackFragment(SettingPasswordFragment())
    }

    private fun showMyProfile() {
        baseShowAddLeftAddBackFragment(SettingMyProfileFragment())
    }

    private fun showOptionImage() {
        baseShowLeftAddBackFragment(TravelingRepresentativeImageFragment())
    }

    private fun showOptionDomestics() {
        val frag = TravelingDomesticsCountryFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_OPTION.name, true)
        frag.arguments = arg
        baseShowLeftAddBackFragment(frag)
    }
    private fun showOptionCountry() {
        val frag = TravelingCountryFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_OPTION.name, true)
        frag.arguments = arg
        baseShowLeftAddBackFragment(frag)
    }

    private fun showOptionTheme() {
        val frag = ThemeFragment()
        val arg = Bundle()
        arg.putBoolean(ArgName.TRAVEL_OPTION.name, true)
        frag.arguments = arg
        baseShowLeftAddBackFragment(frag)
    }

    private fun showTravelingCity() {
        val frag = TravelingCityFragment()
        val arg = Bundle()
        arg.putInt(ArgName.TRAVEL_TYPE.name, getViewModel().travelType)
        frag.arguments = arg
        baseShowTopAddBackFragment(frag)
    }

    private fun showTravelingEnroll() {
        baseShowAddLeftAddBackFragment(TravelingCardEnrollFragment())
    }

    private fun showTravelingEnrollImage() {
        baseShowTopAddBackFragment(TravelingCardImageEnrollFragment())
    }

    private fun showTravelEnroll() {
        baseShowAddLeftAddBackFragment(TravelEnrollFragment())
    }

    private fun showSetting() {
        baseShowTopAddBackFragment(SettingFragment())
    }

    private fun showTraveling() {
        baseShowAddLeftAddBackFragment(TravelingFragment())
    }

    private fun showTravelingCountry() {
        baseShowAddLeftAddBackFragment(TravelingCountryFragment())
    }

    private fun showTravelingRepresentative() {
        baseShowLeftAddBackFragment(TravelingRepresentativeImageFragment())
    }


    private fun showTravelingDetail() {
        baseShowAddLeftAddBackFragment(TravelingDetailFragment())
    }

    private fun showTheme() {
        baseShowTopAddBackFragment(ThemeFragment())
    }

    private fun showCamera() {
        baseShowTopAddBackFragment(CameraFragment())
    }

    private fun showTutorial() {
        baseShowLeftFragment(TutorialFragment())
    }

    private fun showHome() {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.animator.show, 0)
            .replace(R.id.content_frame, TravelFragment(), null)
            .commit()
    }

    private fun showSign() {
        baseShowAddLeftAddBackFragment(SignFragment())
    }

    private fun showSignNormalIn() {
        baseShowAddLeftAddBackFragment(SignInFragment())
    }

    private fun showSignCreate() {
        baseShowAddLeftAddBackFragment(SignUpFragment())
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

}
