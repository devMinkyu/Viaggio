package com.kotlin.viaggio.view.traveling

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@SuppressLint("SimpleDateFormat")
class TravelingFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var gson: Gson

    val travelOfDayListLiveData:MutableLiveData<MutableList<TravelOfDay>> = MutableLiveData()
    val completeLiveData = MutableLiveData<Event<Any>>()

    override fun initialize() {
        super.initialize()
        loadTravelOfDayPaged()

        val disposable = rxEventBus.travelOfDayChange
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it){
                    loadTravelOfDayPaged()
                    rxEventBus.travelOfDayChange.onNext(false)
                }
            }){

            }
        addDisposable(disposable)
        val travelingFinishDisposable = rxEventBus.travelFinish
            .subscribe({
                if(it){
                    rxEventBus.travelFinish.onNext(it.not())
                    completeLiveData.postValue(Event(Any()))
                }
            }){

            }
        addDisposable(travelingFinishDisposable)
        val countryDisposable = rxEventBus.travelOfCountry.subscribe { t ->
            if(TextUtils.isEmpty(t).not()){
                loadTravelOfDayPaged()
                rxEventBus.travelOfCountry.onNext("")
            }
        }
        addDisposable(countryDisposable)
    }

    private fun loadTravelOfDayPaged(){
        val disposable = travelModel.getTravelOfDays()
            .subscribeOn(Schedulers.io())
            .subscribe { t ->
                travelOfDayListLiveData.postValue(t)
            }
        addDisposable(disposable)
    }

    fun setSelectedTravelingOfDay(travelOfDayId: Long?) {
        travelOfDayId?.let {
            val disposable
                    = prefUtilService.putLong(AndroidPrefUtilService.Key.SELECTED_TRAVELING_OF_DAY_ID, it)
                .observeOn(Schedulers.io()).subscribe()
            addDisposable(disposable)
        }
    }
}
