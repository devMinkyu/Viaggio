package com.kotlin.viaggio.view.traveling

import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject


class TravelCardBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    fun modify(index:Int){
        rxEventBus.travelCardChange.onNext(index)
    }
    fun delete(){
        rxEventBus.travelCardChange.onNext(1)
    }
}