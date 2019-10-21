package com.kotlin.viaggio.view.traveling.option

import android.text.TextUtils
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.Android
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.floor

class TravelingDayCountActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val dayCountLiveData: MutableLiveData<Event<Int>> = MutableLiveData()

    val chooseDayCount = ObservableInt(0)
    var traveling = false
    var changeMode = false
    val startDate = Calendar.getInstance()!!
    val chooseDate = Calendar.getInstance()!!
    override fun initialize() {
        super.initialize()
        val disposable = travelLocalModel.getTravel()
            .subscribeOn(Schedulers.io())
            .subscribe ({travelVal ->
                startDate.time = travelVal.startDate!!
                chooseDate.time = travelVal.startDate!!
                if(travelVal.endDate == null){
                    traveling = true
                    val day = floor(
                        ((Calendar.getInstance().time.time - travelVal.startDate!!.time).toDouble() / 1000) / (24 * 60 * 60)
                    ).toInt()
                    dayCountLiveData.postValue(Event(day + 1))
                }else{
                    val day = floor(
                        ((travelVal.endDate!!.time - travelVal.startDate!!.time).toDouble() / 1000) / (24 * 60 * 60)
                    ).toInt()
                    dayCountLiveData.postValue(Event(day + 1))
                }
            }){
                Timber.d(it)
            }
        addDisposable(disposable = disposable)
    }

    fun confirm() :LiveData<Event<Any>>{
        val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()
        chooseDate.add(Calendar.DAY_OF_MONTH, chooseDayCount.get() - 1)
        if(changeMode) {
            var travelCard:TravelCard? = null
            val disposable = travelLocalModel.getTravelCard()
                .flatMapCompletable {list ->
                    list.firstOrNull()?.let {travelCardVal ->
                        travelCardVal.time = chooseDate.time
                        travelCardVal.travelOfDay = chooseDayCount.get()
                        travelCard = travelCardVal
                        travelLocalModel.updateTravelCard(travelCardVal)
                    }?: Completable.complete()
                }
                .andThen {
                    travelCard?.let {travelCard ->
                        val token = travelLocalModel.getToken()
                        val mode = travelLocalModel.getUploadMode()
                        if (TextUtils.isEmpty(token).not() && mode != 2 && travelCard.serverId != 0) {
                            updateWork(travelCard)
                            it.onComplete()
                        } else {
                            it.onComplete()
                        }
                    }?:it.onComplete()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    rxEventBus.travelCardUpdate.onNext(Any())
                    completeLiveDate.value = Event(Any())
                }){
                    Timber.d(it)
                }
            addDisposable(disposable)
        } else {
            chooseDate.add(Calendar.DAY_OF_MONTH, chooseDayCount.get() - 1)
            rxEventBus.travelingOption.onNext(chooseDayCount.get())
            rxEventBus.travelingCalendar.onNext(chooseDate)
            completeLiveDate.value = Event(Any())
        }
        return completeLiveDate
    }
}