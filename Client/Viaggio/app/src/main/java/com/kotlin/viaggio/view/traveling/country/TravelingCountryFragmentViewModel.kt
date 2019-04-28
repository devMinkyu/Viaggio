package com.kotlin.viaggio.view.traveling.country

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Area
import com.kotlin.viaggio.data.`object`.Country
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import timber.log.Timber
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
    val chooseAreaLiveData:MutableLiveData<Event<Any>> = MutableLiveData()

    var travelType = ObservableInt(0)
    val chooseArea:ObservableArrayList<Area> = ObservableArrayList()

    var option = false
    var travel = Travel()
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().getString(R.string.travel_country_json)))
        val type = object : TypeToken<List<Country>>() {}.type

        val countries: List<Country> = gson.fromJson(inputStream, type)
        val list = countries.sortedBy {
            it.country
        }
        countryList.clear()
        countryList.addAll(list)

        continentList.add(appCtx.get().resources.getString(R.string.total))
        val list1= countries.distinctBy {
            it.continent
        }.map {
            it.continent
        }
        continentList.addAll(list1)

        countryLiveData.value = Event(countryList)
        continentLiveData.value = Event(Any())

        if(option){
            val disposable = travelLocalModel.getTravel()
                .subscribe({
                    travel = it
                    if(chooseArea.isNullOrEmpty()){
                        chooseArea.addAll(it.area)
                        chooseAreaLiveData.postValue(Event(Any()))
                    }
                }){
                    Timber.d(it)
                }
            addDisposable(disposable)
        }else{
            val selectedCityDisposable = rxEventBus.travelSelectedCity.subscribe {
                if(chooseArea.isNullOrEmpty()){
                    chooseArea.addAll(it)
                    chooseAreaLiveData.value = Event(Any())
                }
            }
            addDisposable(selectedCityDisposable)
        }

        val typeDisposable = rxEventBus.travelType.subscribe {
            travelType.set(it)
        }
        addDisposable(typeDisposable)

        val areaDisposable = rxEventBus.travelCity.subscribe {
            chooseArea.addAll(it)
            val disList = chooseArea.distinctBy {arraVal ->
                arraVal.city
            }
            chooseArea.clear()
            chooseArea.addAll(disList)
            chooseAreaLiveData.value = Event(Any())
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

    fun confirm(){
        if(option){
            if(travel.id != 0L){
                travel.area = chooseArea
                travel.userExist = false
                val disposable = travelLocalModel.updateTravel(travel)
                    .subscribe {
                        completeLiveData.postValue(Event(Any()))
                    }
                addDisposable(disposable)
            }
        }else{
            rxEventBus.travelSelectedCity.onNext(chooseArea)
            completeLiveData.value = Event(Any())
        }
    }

    fun empty() {
        rxEventBus.travelSelectedCity.onNext(listOf())
    }
}
