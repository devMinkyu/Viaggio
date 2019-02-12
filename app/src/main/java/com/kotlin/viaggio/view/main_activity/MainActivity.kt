package com.kotlin.viaggio.view.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.home.HomeFragment
import com.kotlin.viaggio.view.sign.SignInFragment
import com.kotlin.viaggio.view.tutorial.TutorialFragment

class MainActivity : BaseActivity<MainActivityViewModel>() {
    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            when(firstPath) {
                "home" ->{
                    when(appLinkData.pathSegments?.last()){
                        "main" ->{ showHome() }
                        "login" ->{ showSignIn() }
                    }
                }
                else -> {}
            }
        }
    }

    private fun showTutorial() {
        baseShowFragment(TutorialFragment())
    }
    private fun showHome() {
        baseShowFragment(HomeFragment())
    }
    private fun showSignIn() {
        baseShowAddBackFragment(SignInFragment())
    }

    // 데이터 넘기는 예시
//    val frag = HomeFragment()
//    val arg = Bundle()
//    arg.putString(ArgName.MODE.name, mode)
//    frag.arguments = arg

}
