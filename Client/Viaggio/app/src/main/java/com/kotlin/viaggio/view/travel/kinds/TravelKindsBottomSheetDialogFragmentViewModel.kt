package com.kotlin.viaggio.view.travel.kinds

import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseViewModel
import java.util.*
import javax.inject.Inject


class TravelKindsBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel(){
    var travel = false
    override fun initialize() {
        super.initialize()
        travel = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING, false).blockingGet()
    }

    fun selectKind(kinds: String){
        when(kinds){
            "overseas" ->{
                prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVEL_KINDS, 0).blockingAwait()
            }
            "domestic" ->{
                prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVEL_KINDS, 1).blockingAwait()
            }
        }
    }

    fun travelType(i: Int) {
        rxEventBus.travelType.onNext(i)
    }
}