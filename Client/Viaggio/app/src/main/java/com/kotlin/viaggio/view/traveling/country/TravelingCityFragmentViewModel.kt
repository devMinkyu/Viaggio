package com.kotlin.viaggio.view.traveling.country

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingCityFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    var travelType = 0

    val cityList = mutableListOf<Area>()
    val complete = MutableLiveData<Event<Any>>()
    override fun initialize() {
        super.initialize()
        val disposable = rxEventBus.travelCountry
            .subscribe {
                cityList.clear()
                val list = it.area
                    .map { areaVal ->
                        Area(city = areaVal)
                    }
                cityList.addAll(list)
                complete.value = Event(Any())
            }
        addDisposable(disposable)
    }
}

data class Area(var city:String, var selected:ObservableBoolean = ObservableBoolean(false))