package com.kotlin.viaggio.view.traveling

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.CountryList
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
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
    val completeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
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

    fun changeCountry(position: Int) {
        val country = areaOfCountriesMap[areaList[areaPosition]]!![position]
        prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, country).blockingAwait()
        val day = prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet()

        val travelSingle = travelModel.getTravel()
        val travelOfDaySingle = travelModel.getTravelOfDayCount(day)
        val disposable = Single.zip(travelSingle, travelOfDaySingle, BiFunction<Travel, TravelOfDay, Any> { t1, t2 ->
            t1.entireCountries.add(country)
            t2.dayCountries.add(country)
            travelModel.updateTravel(t1)
            travelModel.updateTravelOfDay(t2)

        }).subscribeOn(Schedulers.io())
            .subscribe({
                rxEventBus.travelOfCountry.onNext(country)
                completeLiveData.postValue(Event(Any()))
            }) {

            }
        addDisposable(disposable = disposable)
    }
}
