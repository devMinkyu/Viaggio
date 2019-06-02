package com.kotlin.viaggio.view.traveling.country

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.Area
import com.kotlin.viaggio.data.obj.Country
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.CountryModel
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class TravelingCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var countryModel: CountryModel

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
        val disposable = countryModel.getCountries(0)
            .subscribe({list ->
                val countries = list.sortedBy { it.country }
                countryList.clear()
                countryList.addAll(countries)
                continentList.add(appCtx.get().resources.getString(R.string.total))
                val list1= countries.distinctBy {
                    it.continent
                }.map {
                    it.continent
                }
                continentList.addAll(list1)

                countryLiveData.postValue(Event(countryList))
                continentLiveData.postValue(Event(Any()))
            }) {
                Timber.d(it)
            }

        addDisposable(disposable)

        val selectedCityDisposable = if(option){
            travelLocalModel.getTravel()
                .subscribe({
                    travel = it
                    if(chooseArea.isNullOrEmpty()){
                        chooseArea.addAll(it.area)
                        chooseAreaLiveData.postValue(Event(Any()))
                    }
                }){
                    Timber.d(it)
                }
        }else{
            rxEventBus.travelSelectedCity.subscribe {
                if(chooseArea.isNullOrEmpty()){
                    chooseArea.addAll(it)
                    chooseAreaLiveData.value = Event(Any())
                }
            }
        }
        addDisposable(selectedCityDisposable)

        val typeDisposable = rxEventBus.travelType.subscribe {
            travelType.set(it)
        }
        addDisposable(typeDisposable)

        val areaDisposable = rxEventBus.travelCity.subscribe {
            chooseArea.addAll(it)
            val disList = chooseArea.distinctBy {areaVal ->
                areaVal.city
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
            if(travel.localId != 0L){
                travel.area = chooseArea
                travel.userExist = false
                val disposable = travelLocalModel.updateTravel(travel)
                    .andThen {
                        val token = travelLocalModel.getToken()
                        val mode = travelLocalModel.getUploadMode()
                        if (TextUtils.isEmpty(token).not() && mode != 2 && travel.serverId != 0) {
                            updateWork(travel)
                            it.onComplete()
                        } else {
                            it.onComplete()
                        }
                    }
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
