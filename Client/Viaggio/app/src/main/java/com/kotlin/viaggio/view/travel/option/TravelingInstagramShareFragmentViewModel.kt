package com.kotlin.viaggio.view.travel.option

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class TravelingInstagramShareFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val choose: MutableList<ObservableBoolean> = mutableListOf()
    val list:MutableList<String> = mutableListOf()

    val share = ObservableBoolean(false)

    val imageNamesListLiveDate:MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val completeLiveDate:MutableLiveData<Event<Any>> = MutableLiveData()

    var chooseIndex:Int = 0
    override fun initialize() {
        super.initialize()
        imageFetch()
    }

    private fun imageFetch() {
        val disposable = travelLocalModel.getTravelCards()
            .subscribeOn(Schedulers.io())
            .subscribe({travelCards ->
                list.clear()
                choose.clear()

                list.addAll(travelCards.map {
                    it.imageNames
                }.flatten().distinct())

                choose.addAll(list.map {
                    ObservableBoolean(false)
                })
                imageNamesListLiveDate.postValue(Event(list))
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }
}
