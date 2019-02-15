package com.kotlin.viaggio.view.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.android.IntentName
import com.kotlin.viaggio.view.camera.CameraFragment
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.home.HomeFragment
import com.kotlin.viaggio.view.ocr.OcrImageFragment
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
                        "image" -> {
                            showOcrImage(intent.getStringExtra(IntentName.OCR_IMAGE_URI_INTENT.name))
                        }
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

    private fun showOcrImage(uri:String?) {
        uri?.let { uriVal ->
            val frag = OcrImageFragment()
            val arg = Bundle()
            arg.putString(ArgName.OCR_IMAGE_URI.name, uriVal)
            frag.arguments = arg
            baseShowAddBackFragment(frag)
        }
    }

    private fun showCamera() {
        baseShowAddBackFragment(CameraFragment())
    }

    private fun showTutorial() {
        baseShowFragment(TutorialFragment())
    }

    private fun showHome() {
        baseShowFragment(HomeFragment())
    }

    private fun showSign() {
        baseShowAddBackFragment(SignFragment())
    }

    private fun showSignNormalIn() {
        baseShowAddBackFragment(SignInFragment())
    }

    private fun showSignCreate() {
        baseShowAddBackFragment(SignUpFragment())
    }
}
