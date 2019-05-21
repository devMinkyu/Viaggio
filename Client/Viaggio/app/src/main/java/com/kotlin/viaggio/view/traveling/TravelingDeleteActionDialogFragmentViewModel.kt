package com.kotlin.viaggio.view.traveling

import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingDeleteActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    var travelCardMode = true
    fun delete() {
        if(travelCardMode){
            rxEventBus.travelCardDelete.onNext(Any())
        } else{
            rxEventBus.travelDelete.onNext(Any())
        }

    }
}