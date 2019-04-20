package com.kotlin.viaggio.view.traveling.country

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.Country
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

    val selectedCities = ObservableArrayList<Area>()
    override fun initialize() {
        super.initialize()
        val disposable = rxEventBus.travelCountry
            .subscribe {
                cityList.clear()
                val list = it.area
                    .map { areaVal ->
                        Area(city = areaVal, continent = it.continent, country = it.country)
                    }
                cityList.addAll(list)
                complete.value = Event(Any())
            }
        addDisposable(disposable)
    }

    fun selectedCity() {
        rxEventBus.travelCity.onNext(selectedCities)
    }
}

data class Area(
    var continent: String,
    var country: String,
    var city:String,
    var selected:ObservableBoolean = ObservableBoolean(false)
)