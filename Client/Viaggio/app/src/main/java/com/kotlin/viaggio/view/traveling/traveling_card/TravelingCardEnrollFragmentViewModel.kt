package com.kotlin.viaggio.view.traveling.traveling_card

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val imagePathList: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()

    val chooseCountList:MutableList<ObservableInt> = mutableListOf()
    var entireChooseCount:Int = 0
    val imageAllList:MutableList<String> = mutableListOf()
    val imageChooseList:MutableList<String> = mutableListOf()
    override fun initialize() {
        super.initialize()
        for (s in travelModel.imageAllPath()) {
            imageAllList.add(s)
            chooseCountList.add(ObservableInt(0))
        }
        imagePathList.value = Event(imageAllList)
    }
}
