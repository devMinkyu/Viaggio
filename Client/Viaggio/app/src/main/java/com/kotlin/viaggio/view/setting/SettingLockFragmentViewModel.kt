package com.kotlin.viaggio.view.setting

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class SettingLockFragmentViewModel @Inject constructor() : BaseViewModel() {

    val completeLiveData = MutableLiveData<Event<Any>>()

    val lockApp = ObservableBoolean(false)
    val fingerPrintLockApp = ObservableBoolean(false)
    override fun initialize() {
        super.initialize()

        lockApp.set(prefUtilService.getBool(AndroidPrefUtilService.Key.LOCK_APP).blockingGet())
        fingerPrintLockApp.set(prefUtilService.getBool(AndroidPrefUtilService.Key.FINGER_PRINT_LOCK_APP).blockingGet())
    }

}
