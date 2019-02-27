package com.kotlin.viaggio.view.traveling.traveling_card

import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingTransportationBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    override fun initialize() {
        super.initialize()
    }

    fun selectedTransportation(string: String) {
        rxEventBus.travelCardTransportation.onNext(string)
    }
}
