package com.kotlin.viaggio.view.traveling.country

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Area
import com.kotlin.viaggio.data.`object`.Country
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class TravelingCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson

    private val countryList:MutableList<Country> = mutableListOf()
    val continentList:MutableList<String> = mutableListOf()

    val countryLiveData:MutableLiveData<Event<List<Country>>> = MutableLiveData()
    val continentLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
    val completeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()

    var travelType = ObservableInt(0)

    val chooseArea = mutableListOf<Area>()
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().getString(R.string.travel_country_json)))
        val type = object : TypeToken<List<Country>>() {}.type

        val countries: List<Country> = gson.fromJson(inputStream, type)
        countryList.clear()
        countryList.addAll(countries)

        continentList.add(appCtx.get().resources.getString(R.string.total))
        countries.map {
            continentList.add(it.continent)
        }
        val result = continentList.distinct()
        continentList.clear()
        continentList.addAll(result)

        countryLiveData.value = Event(countryList)
        continentLiveData.value = Event(Any())

        val typeDisposable = rxEventBus.travelType.subscribe {
            travelType.set(it)
        }
        addDisposable(typeDisposable)

        val areaDisposable = rxEventBus.travelCity.subscribe {
            chooseArea.addAll(it)
            val result = chooseArea.distinct()
            chooseArea.clear()
            chooseArea.addAll(result)
        }
        addDisposable(areaDisposable)
    }

    fun selectedCountry(country: String?) {
        country?.let {countryVal ->
            val selectedCountry = countryList.firstOrNull {
                it.country == countryVal
            }
            selectedCountry?.let {
                rxEventBus.travelCountry.onNext(it)
            }
        }
    }

    fun chooseContinent(position: Int) {
        val continent = continentList[position]
        when(position){
            0 ->{
                countryLiveData.value = Event(countryList)
            }
            else ->{
                val list = countryList.filter {
                    it.continent == continent
                }
                countryLiveData.value = Event(list)
            }
        }
    }
}
