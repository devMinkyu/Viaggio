package com.kotlin.viaggio.view.traveling.country

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.obj.Area
import com.kotlin.viaggio.data.obj.Country
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.UpdateTravelWorker
import timber.log.Timber
import java.io.InputStreamReader
import javax.inject.Inject

class TravelingDomesticsCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val domesticsLiveData: MutableLiveData<Event<Any>> = MutableLiveData()
    val completeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()

    var groupDomestics: List<Country> = listOf()
    val selectedCities = ObservableArrayList<Area>()
    var check = false
    var option = false
    var travel = Travel()
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open("domestics.json"))
        val type = object : TypeToken<List<Country>>() {}.type
        groupDomestics = gson.fromJson(inputStream, type)

        domesticsLiveData.value = Event(Any())

        if (option) {
            val disposable = travelLocalModel.getTravel()
                .subscribe({
                    travel = it
                    if (selectedCities.isNullOrEmpty()) {
                        selectedCities.addAll(it.area)
                        domesticsLiveData.postValue(Event(Any()))
                    }
                }) {
                    Timber.d(it)
                }
            addDisposable(disposable)
        } else {
            val disposable = rxEventBus.travelSelectedCity.subscribe {
                if (check.not()) {
                    selectedCities.clear()
                    selectedCities.addAll(it)
                    domesticsLiveData.value = Event(Any())
                }
            }
            addDisposable(disposable)
        }
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
