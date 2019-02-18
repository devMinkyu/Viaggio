package com.kotlin.viaggio.view.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.home.HomeFragment
import com.kotlin.viaggio.view.sign.SignFragment
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.sign.SignUpFragment
import com.kotlin.viaggio.view.tutorial.TutorialFragment

class MainActivity : BaseActivity<MainActivityViewModel>() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        if(getViewModel().checkTutorial()){
//            showHome()
//        }else{
//            showTutorial()
//        }
        showTutorial()

        handleIntent(intent)
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
                    }
                }
                "login" -> {
                    when (appLinkData.pathSegments?.last()) {
                        "normal" -> showSignNormalIn()
                        "create" -> showSignCreate()

                    }
                }
                else -> {
                }
            }
        }
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
