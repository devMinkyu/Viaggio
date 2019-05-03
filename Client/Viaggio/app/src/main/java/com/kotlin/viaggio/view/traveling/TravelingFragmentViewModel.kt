package com.kotlin.viaggio.view.traveling

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
import timber.log.Timber
import javax.inject.Inject


class TravelingFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson

    val showTravelCard = MutableLiveData<Event<Boolean>>()
    val completeLiveData = MutableLiveData<Event<Any>>()
    val changeCardLiveData = MutableLiveData<Event<Any>>()
    lateinit var travelCardPagedLiveData: LiveData<PagedList<TravelCard>>

    val title:ObservableField<String> = ObservableField("")
    val notEmpty:ObservableBoolean = ObservableBoolean(false)

    var modifyLocation = IntArray(2)
    override fun initialize() {
        super.initialize()
        loadTravelOfDayPaged()
        val travelDisposable = travelLocalModel.getTravel()
            .subscribeOn(Schedulers.io())
            .subscribe({
                title.set(it.title)
            }){
                Timber.d(it)
            }
        addDisposable(travelDisposable)

        val disposable = rxEventBus.travelCardUpdate
            .subscribeOn(Schedulers.io())
            .subscribe({
                travelCardPagedLiveData.value?.dataSource?.invalidate()
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)

        val changeDisposable = rxEventBus.travelCardChange
            .subscribe {
                changeCardLiveData.value = Event(Any())
            }
        addDisposable(changeDisposable)
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
