package com.kotlin.viaggio.view.traveling.option

import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TravelingDayCountActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()
    val dayCountLiveData: MutableLiveData<Event<Int>> = MutableLiveData()

    val chooseDayCount = ObservableInt(0)
    var traveling = false
    override fun initialize() {
        super.initialize()
        val disposable = travelLocalModel.getTravel()
            .subscribeOn(Schedulers.io())
            .subscribe ({travelVal ->
                if(travelVal.endDate == null){
                    traveling = true
                    val day = Math.floor(
                        ((Calendar.getInstance().time.time - travelVal.startDate!!.time).toDouble() / 1000) / (24 * 60 * 60)
                    ).toInt()
                    dayCountLiveData.postValue(Event(day + 1))
                }else{
                    val day = Math.floor(
                        ((travelVal.endDate!!.time - travelVal.startDate!!.time).toDouble() / 1000) / (24 * 60 * 60)
                    ).toInt()
                    dayCountLiveData.postValue(Event(day + 1))
                }
            }){
                Timber.d(it)
            }
        addDisposable(disposable = disposable)
    }

    fun confirm() {
        rxEventBus.travelingOption.onNext(chooseDayCount.get())
        completeLiveDate.value = Event(Any())
    }
}