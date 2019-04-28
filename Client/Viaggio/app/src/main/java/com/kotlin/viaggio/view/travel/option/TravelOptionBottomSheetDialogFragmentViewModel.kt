package com.kotlin.viaggio.view.travel.option

import androidx.databinding.ObservableBoolean
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject


class TravelOptionBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    val traveling = ObservableBoolean(false)
    override fun initialize() {
        super.initialize()

        val travelIngId = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID).blockingGet()
        val selectedTravelId = prefUtilService.getLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID).blockingGet()
        if(travelIngId == selectedTravelId){
            traveling.set(true)
        }
    }
}