package com.kotlin.viaggio.view.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.home.HomeFragment
import com.kotlin.viaggio.view.setting.SettingFragment
import com.kotlin.viaggio.view.sign.SignFragment
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.sign.SignUpFragment
import com.kotlin.viaggio.view.theme.ThemeFragment
import com.kotlin.viaggio.view.tutorial.TutorialFragment
import org.jetbrains.anko.toast

class MainActivity : BaseActivity<MainActivityViewModel>() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(getViewModel().checkTutorial()){
            showHome()
        }else{
            showTutorial()
        }

        handleIntent(intent)

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
        if(supportFragmentManager.backStackEntryCount == 0){
            getViewModel().backButtonSubject.onNext(System.currentTimeMillis())
        }else{
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
            val firstPath = appLinkData?.pathSegments?.get(0)
            when (firstPath) {
                "home" -> {
                    when (appLinkData.pathSegments?.last()) {
                        "main" -> showHome()
                        "login" -> showSign()
                        "camera" -> showCamera()
                        "theme" -> showTheme()
                    }
                }
                "login" -> {
                    when (appLinkData.pathSegments?.last()) {
                        "normal" -> showSignNormalIn()
                        "create" -> showSignCreate()
                    }
                }
                "setting" ->{
                    when (appLinkData.pathSegments?.last()) {
                        "main" -> showSetting()
                    }
                }
                else -> {
                }
            }
        }
    }

    private fun showTheme() {
        baseShowLeftAddBackFragment(ThemeFragment())
    }

    private fun showSetting() {
        baseShowTopAddBackFragment(SettingFragment())
    }

    private fun showCamera() {
        baseShowTopAddBackFragment(CameraFragment())
    }

    private fun showTutorial() {
        baseShowLeftFragment(TutorialFragment())
    }

    private fun showHome() {
        baseShowLeftFragment(HomeFragment())
    }

    private fun showSign() {
        baseShowLeftAddBackFragment(SignFragment())
    }

    private fun showSignNormalIn() {
        baseShowLeftAddBackFragment(SignInFragment())
    }

    private fun showSignCreate() {
        baseShowLeftAddBackFragment(SignUpFragment())
    }
}
