package com.kotlin.viaggio.view.camera

import androidx.databinding.ObservableField
import com.kotlin.viaggio.event.RxEventBus
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class OcrImageActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    var chooseCountry = ObservableField<String>("")
    override fun initialize() {
        super.initialize()
        val disposable = RxEventBus().toObservable().subscribe {
            chooseCountry.set(it as String)
        }
        disposable.dispose()
    }
}