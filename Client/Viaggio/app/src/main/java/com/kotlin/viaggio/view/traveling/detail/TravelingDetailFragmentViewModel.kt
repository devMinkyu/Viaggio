package com.kotlin.viaggio.view.traveling.detail

import android.annotation.SuppressLint
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.ArrayList
import javax.inject.Inject

class TravelingDetailFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    var travelOfDay:TravelOfDay = TravelOfDay()

    val travelingOfDayCount:ObservableInt = ObservableInt(0)
    val travelingOfDay:ObservableField<String> = ObservableField("")
    val travelingOfDayTheme:ObservableField<String> = ObservableField("")
    val isTheme:ObservableBoolean = ObservableBoolean(false)
    val existTravelCard:ObservableBoolean = ObservableBoolean(false)

    lateinit var travelCardPagedLiveData: LiveData<PagedList<TravelCard>>
    val travelOfDayImageChange:MutableLiveData<Event<String>> = MutableLiveData()

    @SuppressLint("SimpleDateFormat")
    override fun initialize() {
        super.initialize()
        val disposable = travelModel.getTravelOfDay()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .flatMap {
                travelOfDay = it
                travelingOfDayCount.set(it.travelOfDay)
                travelingOfDay.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).format(it.date))
                if(travelOfDay.theme.isNotEmpty()){
                    isTheme.set(true)
                    travelingOfDayTheme.set("")
                    for (s in travelOfDay.theme) {
                        travelingOfDayTheme.set("${travelingOfDayTheme.get()} $s")
                        travelingOfDayTheme.set(travelingOfDayTheme.get()?.trim())
                    }
                    travelModel.updateTravelOfDay(travelOfDay)
                }
                Single.just(travelOfDay)
            }
            .subscribe ({travelOfDay ->
                val disposable = rxEventBus.travelOfDayTheme
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe{
                        if(it.isNotEmpty()){
                            travelOfDay.theme = it as ArrayList<String>
                            if(travelOfDay.theme.isNotEmpty()){
                                isTheme.set(true)
                                travelingOfDayTheme.set("")
                                for (s in travelOfDay.theme) {
                                    travelingOfDayTheme.set("${travelingOfDayTheme.get()} $s")
                                    travelingOfDayTheme.set(travelingOfDayTheme.get()?.trim())
                                }
                                travelModel.updateTravelOfDay(travelOfDay)
                            }
                            rxEventBus.travelOfDayTheme.onNext(listOf())
                        }
                    }
                addDisposable(disposable)
            }){

            }
        addDisposable(disposable)
        loadTravelCard()

        val updateDisposable = rxEventBus.travelCardUpdate
            .observeOn(Schedulers.io())
            .subscribe({
                loadTravelCard()
            }){

            }
        addDisposable(updateDisposable)
        val imageChangeDisposable = rxEventBus.travelOfDayImage
            .observeOn(Schedulers.io())
            .subscribe({
                travelOfDayImageChange.postValue(Event(it))
            }){

            }
        addDisposable(imageChangeDisposable)
    }
    fun loadTravelCard(){
        val factory: DataSource.Factory<Int, TravelCard>
                = travelModel.getTravelCardsPager()
        val pagedListBuilder: LivePagedListBuilder<Int, TravelCard> = LivePagedListBuilder<Int, TravelCard>(factory,
            20)
        travelCardPagedLiveData = pagedListBuilder.build()
    }
}
