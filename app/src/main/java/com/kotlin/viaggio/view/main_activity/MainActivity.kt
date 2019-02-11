package com.kotlin.viaggio.view.main_activity

import android.os.Bundle
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseActivity
import com.kotlin.viaggio.view.tutorial.TutorialFragment

class MainActivity : BaseActivity<MainActivityViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showTutorial()
    }

    private fun showTutorial() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, TutorialFragment(), null)
            .commit()
    }
}
