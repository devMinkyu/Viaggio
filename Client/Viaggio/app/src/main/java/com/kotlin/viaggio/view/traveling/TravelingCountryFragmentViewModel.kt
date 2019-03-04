package com.kotlin.viaggio.view.traveling

import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    override fun initialize() {
        super.initialize()

    }
}
