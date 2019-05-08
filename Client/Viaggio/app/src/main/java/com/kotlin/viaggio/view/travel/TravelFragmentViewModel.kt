package com.kotlin.viaggio.view.travel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class TravelFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val travelListLiveData = MutableLiveData<Event<List<Travel>>>()

    val travelList = mutableListOf<Travel>()
    val travelOption = mutableListOf<String>()
    var isTravelRefresh = false

    val traveling = ObservableField<String>("")
    val travelingKind = ObservableField<String>("")

    override fun initialize() {
        super.initialize()
        fetchData()
        travelOption.add(appCtx.get().resources.getString(R.string.total))
        travelOption.add(appCtx.get().resources.getString(R.string.travel_overseas))
        travelOption.add(appCtx.get().resources.getString(R.string.travel_domestic))

        if(prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()){
            traveling.set(appCtx.get().resources.getString(R.string.traveling_notice))
            when(prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_KINDS).blockingGet()){
                0 ->{
                  travelingKind.set(travelOption[1])
                }
                1 ->{
                    travelingKind.set(travelOption[2])
                }
            }
        }else{
            traveling.set(appCtx.get().resources.getString(R.string.travel_notice))
        }
        val disposable = rxEventBus.travelCardUpdate
            .subscribe {
                fetchData()
            }
        addDisposable(disposable)
        val travelUpdateDisposable = rxEventBus.travelUpdate
            .subscribe {
                fetchData()
            }
        addDisposable(travelUpdateDisposable)
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
                Timber.d(it)
            }

        addDisposable(disposable)
    }

    fun selectedTravelId(id: Long) {
        prefUtilService.putLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID, id).blockingAwait()
    }

    fun optionCheck(position:Int){
        when(position){
            0 ->{
                travelListLiveData.postValue(Event(travelList))
            }
            1 ->{
                val list = travelList.filter { it.travelKind == 0 }
                travelListLiveData.postValue(Event(list))
            }
            2 ->{
                val list = travelList.filter { it.travelKind == 1 }
                travelListLiveData.postValue(Event(list))
            }
        }
    }
}
