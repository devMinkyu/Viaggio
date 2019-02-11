package com.kotlin.viaggio.view.main

import android.os.Bundle
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseActivity

class MainActivity : BaseActivity<MainActivityViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
