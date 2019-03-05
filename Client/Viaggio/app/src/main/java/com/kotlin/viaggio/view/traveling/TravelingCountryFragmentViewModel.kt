package com.kotlin.viaggio.view.traveling

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.CountryList
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class TravelingCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var gson: Gson

    var chooseContinent = ObservableBoolean(false)
    var chooseArea = ObservableBoolean(false)

    var continentPosition = 0
    var areaPosition = 0

    private val continentList:MutableList<String> = mutableListOf()
    private val areaList:MutableList<String> = mutableListOf()
    private val continentOfAreasMap:MutableMap<String, MutableList<String>> = mutableMapOf()
    private val areaOfCountriesMap:MutableMap<String, MutableList<String>> = mutableMapOf()

    val continentLiveData:MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val areaLiveData:MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val countryLiveData:MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().getString(R.string.travel_country_json)))
        val countries: CountryList = gson.fromJson(inputStream, CountryList::class.java)

        for (datum in countries.data) {
            continentList.add(datum.continent)
            if(!continentOfAreasMap.containsKey(datum.continent)){
                continentOfAreasMap[datum.continent] = mutableListOf()
                for (area in datum.areas) {
                    continentOfAreasMap[datum.continent]?.add(area.area)
                    if(!areaOfCountriesMap.containsKey(area.area)){
                        areaOfCountriesMap[area.area] = area.country.toMutableList()
                    }
                }
            }
        }
        continentLiveData.value = Event(continentList)
    }

    fun showArea(position: Int) {
        chooseContinent.set(true)
        chooseArea.set(false)
        continentPosition = position
        if(continentOfAreasMap.containsKey(continentList[position])){
            areaLiveData.value = Event(continentOfAreasMap[continentList[position]]!!)
            areaList.clear()
            areaList.addAll(continentOfAreasMap[continentList[position]]!!)
        }
    }

    fun showCountry(position: Int) {
        chooseArea.set(true)
        areaPosition = position
        if(areaOfCountriesMap.containsKey(areaList[position])){
            countryLiveData.value = Event(areaOfCountriesMap[areaList[position]]!!)
        }
    }
}
