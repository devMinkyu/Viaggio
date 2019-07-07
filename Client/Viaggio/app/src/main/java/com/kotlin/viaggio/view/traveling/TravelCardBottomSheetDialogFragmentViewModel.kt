package com.kotlin.viaggio.view.traveling

import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject


class TravelCardBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    fun modify(){
        rxEventBus.travelCardChange.onNext(0)
    }
    fun delete(){
        rxEventBus.travelCardChange.onNext(1)
    }

    fun imageModify() {
        rxEventBus.travelCardChange.onNext(2)
    }
}