package com.kotlin.viaggio.view.main_activity

import com.kotlin.viaggio.android.TimeHelper
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class MainActivityViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var timeHelper: TimeHelper

    override fun initialize() {
        super.initialize()
        val traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
        if (traveling) {
            timeHelper.timeCheckOfDay()
        }
    }

    fun checkTutorial() = prefUtilService.getBool(AndroidPrefUtilService.Key.TUTORIAL_CHECK).blockingGet() ?: false
}