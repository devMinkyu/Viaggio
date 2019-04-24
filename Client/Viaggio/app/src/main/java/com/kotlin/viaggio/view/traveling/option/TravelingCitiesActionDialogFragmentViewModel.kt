package com.kotlin.viaggio.view.traveling.option

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.`object`.Area
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.TimeCheckWorker
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TravelingCitiesActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()
    val areaListLiveData: MutableLiveData<Event<Any>> = MutableLiveData()

    val areaList = mutableListOf<Area>()
    val chooseArea = ObservableField<Area>()
    override fun initialize() {
        super.initialize()

        val disposable = travelLocalModel.getTravel()
            .subscribeOn(Schedulers.io())
            .subscribe ({travelVal ->
                areaList.addAll(travelVal.area.map {area ->
                    area.selected.set(false)
                    area
                })

                val country = prefUtilService.getString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES).blockingGet()

                val item = areaList.first{area ->
                    "${area.country}_${area.city}" == country
                }
                item.selected.set(true)
                chooseArea.set(item)
                areaListLiveData.postValue(Event(Any()))
            }){
                Timber.d(it)
            }
        addDisposable(disposable = disposable)
    }

    fun confirm() {
        prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, "${chooseArea.get()!!.country}_${chooseArea.get()!!.city}").blockingAwait()
        rxEventBus.travelingOption.onNext(chooseArea.get()!!)
        completeLiveDate.value = Event(Any())
    }
}