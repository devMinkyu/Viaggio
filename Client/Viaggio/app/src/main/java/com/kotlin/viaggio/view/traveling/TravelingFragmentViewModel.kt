package com.kotlin.viaggio.view.traveling

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class TravelingFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson

    val showTravelCard = MutableLiveData<Event<Boolean>>()
    val completeLiveData = MutableLiveData<Event<Any>>()
    lateinit var travelCardPagedLiveData: LiveData<PagedList<TravelCard>>

    val title:ObservableField<String> = ObservableField("")
    val notEmpty:ObservableBoolean = ObservableBoolean(false)
    override fun initialize() {
        super.initialize()
        loadTravelOfDayPaged()
        val travelDisposable = travelLocalModel.getTravel()
            .subscribeOn(Schedulers.io())
            .subscribe({
                title.set(it.title)
            }){

            }
        addDisposable(travelDisposable)

        val disposable = rxEventBus.travelOfDayChange
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it) {
                    travelCardPagedLiveData.value?.dataSource?.invalidate()
                    rxEventBus.travelOfDayChange.onNext(false)
                }
            }) {

            }
        addDisposable(disposable)
        val travelingFinishDisposable = rxEventBus.travelFinish
            .subscribe({
                if (it) {
                    rxEventBus.travelFinish.onNext(it.not())
                    completeLiveData.postValue(Event(Any()))
                }
            }) {

            }
        addDisposable(travelingFinishDisposable)
    }

    private fun loadTravelOfDayPaged() {
        val factory = travelLocalModel.getTravelCardsPaging()
        val pagedListBuilder = LivePagedListBuilder<Int, TravelCard>(
            factory,
            10
        )
        travelCardPagedLiveData = pagedListBuilder.build()
    }

    fun setSelectedTravelCard(travelCardId: Long?) {
        travelCardId?.let {
            prefUtilService.putLong(AndroidPrefUtilService.Key.SELECTED_TRAVELING_CARD_ID, it).blockingAwait()
        }
    }
}
