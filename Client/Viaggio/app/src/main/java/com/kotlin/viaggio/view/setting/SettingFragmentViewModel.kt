package com.kotlin.viaggio.view.setting

import androidx.databinding.ObservableField
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class SettingFragmentViewModel @Inject constructor() : BaseViewModel(){

    val name = ObservableField<String>("")
    override fun initialize() {
        super.initialize()

        name.set(prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet())
    }
}
