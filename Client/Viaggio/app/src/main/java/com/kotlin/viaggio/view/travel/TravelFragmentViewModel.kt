package com.kotlin.viaggio.view.travel

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TravelFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val travelListLiveData = MutableLiveData<Event<List<Travel>>>()

    val travelList = mutableListOf<Travel>()

    var isTravelRefresh = false

    override fun initialize() {
        super.initialize()
        fetchData()
    }
    fun fetchData(){
        val disposable = travelLocalModel.getTravels()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                travelList.clear()
                travelList.addAll(it)
                travelListLiveData.postValue(Event(travelList))
            }){
                travelListLiveData.postValue(Event(travelList))
            }

        addDisposable(disposable)
    }

    fun selectedTravelId(id: Long) {
        prefUtilService.putLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID, id).blockingAwait()
    }
}
