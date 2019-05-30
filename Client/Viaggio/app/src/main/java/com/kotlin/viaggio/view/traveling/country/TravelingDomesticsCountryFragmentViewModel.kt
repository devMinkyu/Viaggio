package com.kotlin.viaggio.view.traveling.country

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.Area
import com.kotlin.viaggio.data.obj.Country
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.CountryModel
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class TravelingDomesticsCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var countryModel: CountryModel

    val domesticsLiveData: MutableLiveData<Event<Any>> = MutableLiveData()
    val completeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()

    var groupDomestics: List<Country> = listOf()
    val selectedCities = ObservableArrayList<Area>()
    var check = false
    var option = false
    var travel = Travel()
    override fun initialize() {
        super.initialize()
        val disposable = countryModel.getCountries(1)
            .subscribe({
                groupDomestics = it
                domesticsLiveData.postValue(Event(Any()))
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)


        val selectedDisposable = if (option) {
            travelLocalModel.getTravel()
                .subscribe({
                    travel = it
                    if (selectedCities.isNullOrEmpty()) {
                        selectedCities.addAll(it.area)
                        domesticsLiveData.postValue(Event(Any()))
                    }
                }) {
                    Timber.d(it)
                }
        } else {
            rxEventBus.travelSelectedCity.subscribe {
                if (check.not()) {
                    selectedCities.clear()
                    selectedCities.addAll(it)
                    domesticsLiveData.value = Event(Any())
                }
            }
        }
        addDisposable(selectedDisposable)
    }

    fun selectedCity() {
        if (option) {
            if(travel.localId != 0L){
                travel.area = selectedCities
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
                    }.subscribe {
                        completeLiveData.postValue(Event(Any()))
                    }
                addDisposable(disposable)
            }
        } else {
            check = true
            rxEventBus.travelSelectedCity.onNext(selectedCities)
            completeLiveData.postValue(Event(Any()))
        }
    }
}
