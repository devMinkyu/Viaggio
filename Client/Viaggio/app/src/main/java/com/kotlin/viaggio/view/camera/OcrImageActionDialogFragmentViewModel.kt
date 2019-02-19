package com.kotlin.viaggio.view.camera

import androidx.databinding.ObservableField
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.RxEventBus
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject

class OcrImageActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var rxEventBus: RxEventBus

    var chooseCountry = ObservableField<String>("")
    override fun initialize() {
        super.initialize()
        val disposable = rxEventBus.travelOfCountry.subscribe {
            chooseCountry.set(it)
        }
        addDisposable(disposable)
    }

    fun startTravel() {
        var disposable = prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, true).subscribe()
        addDisposable(disposable)
        disposable = prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 1).subscribe()
        addDisposable(disposable)
        disposable = prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).subscribe()
        addDisposable(disposable)

        rxEventBus.travelOfGo.onNext(Any())
    }
}