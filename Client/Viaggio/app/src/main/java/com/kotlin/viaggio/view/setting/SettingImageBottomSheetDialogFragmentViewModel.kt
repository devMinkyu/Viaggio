package com.kotlin.viaggio.view.setting

import androidx.databinding.ObservableInt
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject


class SettingImageBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    val mode = ObservableInt(0)
    override fun initialize() {
        super.initialize()
        mode.set(prefUtilService.getInt(AndroidPrefUtilService.Key.IMAGE_MODE).blockingGet())
    }

    fun check(mode: Int) {
        prefUtilService.putInt(AndroidPrefUtilService.Key.IMAGE_MODE, mode).blockingAwait()
    }
}