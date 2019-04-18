package com.kotlin.viaggio.view.traveling

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.R
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
    private val continentList:MutableList<String> = mutableListOf()

    val countryLiveData:MutableLiveData<Event<List<String>>> = MutableLiveData()
    val completeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().getString(R.string.travel_country_json)))
        val type = object : TypeToken<List<Country>>() {}.type

        val countries: List<Country> = gson.fromJson(inputStream, type)
        countryList.clear()
        countryList.addAll(countries)

        continentList.add(appCtx.get().resources.getString(R.string.total))
        val list = countries.map {
            continentList.add(it.continent)
            it.country
        }

        countryLiveData.value = Event(list)
    }
}
