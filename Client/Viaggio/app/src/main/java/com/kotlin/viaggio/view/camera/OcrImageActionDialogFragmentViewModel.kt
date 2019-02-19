package com.kotlin.viaggio.view.camera

import androidx.databinding.ObservableField
import com.kotlin.viaggio.event.RxEventBus
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.functions.Consumer
import javax.inject.Inject

class OcrImageActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var rxEventBus: RxEventBus

    var chooseCountry = ObservableField<String>("")
    override fun initialize() {
        super.initialize()
        val disposable = rxEventBus.bus.subscribe {
            chooseCountry.set(it as String)
        }
        addDisposable(disposable)
    }
}