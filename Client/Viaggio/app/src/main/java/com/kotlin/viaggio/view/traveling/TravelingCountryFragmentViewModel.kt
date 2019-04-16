package com.kotlin.viaggio.view.traveling

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Country
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import javax.inject.Inject

class TravelingCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson

    var chooseContinent = ObservableBoolean(false)
    var chooseArea = ObservableBoolean(false)

    var continentPosition = 0
    var areaPosition = 0

    private val continentList:MutableList<String> = mutableListOf()
    private val areaList:MutableList<String> = mutableListOf()
    private val continentOfAreasMap:MutableMap<String, List<String>> = mutableMapOf()
    private val areaOfCountriesMap:MutableMap<String, List<String>> = mutableMapOf()

    val continentLiveData:MutableLiveData<Event<List<String>>> = MutableLiveData()
    val areaLiveData:MutableLiveData<Event<List<String>>> = MutableLiveData()
    val countryLiveData:MutableLiveData<Event<List<String>>> = MutableLiveData()
    val completeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().getString(R.string.travel_country_json)))
        val type = object : TypeToken<List<Country>>() {}.type

        val countries: List<Country> = gson.fromJson(inputStream, type)
        val result = countries.associate {country ->
            continentList.add(country.continent)
            val ares = country.areas
            val areaResult = ares.associate { it.area to it.country}
            areaOfCountriesMap.putAll(areaResult)
            country.continent to ares.map { it.area }
        }
        continentOfAreasMap.putAll(result)

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

    fun changeCountry(position: Int) {
        val country = areaOfCountriesMap[areaList[areaPosition]]!![position]
        prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, country).blockingAwait()
        val day = prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet()

        val travelSingle = travelLocalModel.getTravel()
        val travelOfDaySingle = travelLocalModel.getTravelOfDayCount(day)
        val disposable = Single.zip(travelSingle, travelOfDaySingle, BiFunction<Travel, TravelOfDay, Any> { t1, t2 ->
            t1.entireCountries.add(country)
            t2.dayCountries.add(country)

            travelLocalModel.updateTravel(t1)
            travelLocalModel.updateTravelOfDay(t2).subscribe()
        }).subscribeOn(Schedulers.io())
            .subscribe({
                rxEventBus.travelOfCountry.onNext(country)
                completeLiveData.postValue(Event(Any()))
            }) {

            }
        addDisposable(disposable = disposable)
    }
}
