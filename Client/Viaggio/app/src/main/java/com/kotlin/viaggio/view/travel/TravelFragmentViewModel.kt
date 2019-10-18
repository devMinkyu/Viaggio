package com.kotlin.viaggio.view.travel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class TravelFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var userModel: UserModel

    val travelListLiveData = MutableLiveData<Event<List<Travel>>>()

    val travelList = mutableListOf<Travel>()
    val travelOption = mutableListOf<String>()
    var isTravelRefresh = false
    var chooseNum:Int? = null

    val traveling = ObservableField("")
    val travelingKind = ObservableField("")
    var travelingId = 0L

    override fun initialize() {
        super.initialize()
        fetchData()
        travelOption.add(appCtx.get().resources.getString(R.string.total))
        travelOption.add(appCtx.get().resources.getString(R.string.travel_overseas))
        travelOption.add(appCtx.get().resources.getString(R.string.travel_domestic))
        travelOption.add(appCtx.get().resources.getString(R.string.travel_day_trip))

        travelingId = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID).blockingGet()

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
                travelingId = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID).blockingGet()
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
        val list = when(position){
            0 -> travelList
            1 -> travelList.filter { it.travelKind == 0 }
            2 -> travelList.filter { it.travelKind == 1 }
            3 -> travelList.filter { it.travelKind == 2 }
            else -> travelList
        }
        travelListLiveData.postValue(Event(list))
    }
}
