package com.kotlin.viaggio.view.main_activity

import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(): BaseViewModel() {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    fun checkTutorial() = prefUtilService.getBool(AndroidPrefUtilService.Key.TUTORIAL_CHECK).blockingGet()?:false
}