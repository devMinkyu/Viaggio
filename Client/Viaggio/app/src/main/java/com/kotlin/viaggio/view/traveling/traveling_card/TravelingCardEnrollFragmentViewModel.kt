package com.kotlin.viaggio.view.traveling.traveling_card

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val imagePathList: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()

    override fun initialize() {
        super.initialize()
        imagePathList.value = Event(travelModel.imageAllPath())
    }
}
