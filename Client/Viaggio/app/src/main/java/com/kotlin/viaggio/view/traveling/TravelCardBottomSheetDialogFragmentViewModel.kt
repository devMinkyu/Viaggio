package com.kotlin.viaggio.view.traveling

import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject


class TravelCardBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    fun modify(){
        rxEventBus.travelCardChange.onNext(true)
    }
    fun delete(){
        rxEventBus.travelCardChange.onNext(false)
    }
}