package com.kotlin.viaggio.view.traveled

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TravelFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val travelListLiveData = MutableLiveData<Event<List<Travel>>>()

    val travelList = mutableListOf<Travel>()

    override fun initialize() {
        super.initialize()
        val disposable = travelModel.getTravels()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                travelList.addAll(it)
                travelListLiveData.postValue(Event(travelList))
            }){
                travelListLiveData.postValue(Event(travelList))
            }

        addDisposable(disposable)
    }
}
