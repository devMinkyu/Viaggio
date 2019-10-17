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

    fun selectKind(kinds: Int){
        prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVEL_KINDS, kinds).blockingAwait()
    }
}