package com.kotlin.viaggio.view.tutorial.intro

import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class IntroFragmentViewModel @Inject constructor(): BaseViewModel() {
    companion object {
        val TAG: String = IntroFragmentViewModel::class.java.simpleName
    }
}