package com.kotlin.viaggio.view.traveling.day_trip

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class TravelDayTripFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    lateinit var travelDayTripPagedLiveData: LiveData<PagedList<TravelCard>>

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
                Timber.d(it)
            }
        addDisposable(travelDisposable)

        val disposable = rxEventBus.travelCardUpdate
            .subscribeOn(Schedulers.io())
            .subscribe({
                travelDayTripPagedLiveData.value?.dataSource?.invalidate()
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)
    }

    private fun loadTravelOfDayPaged() {
        val factory = travelLocalModel.getTravelDayTripPaging()
        val pagedListBuilder = LivePagedListBuilder<Int, TravelCard>(
            factory,
            10
        )
        travelDayTripPagedLiveData = pagedListBuilder.build()
    }

    fun setSelectedTravelCard(travelCardId: Long?) {
        travelCardId?.let {
            prefUtilService.putLong(AndroidPrefUtilService.Key.SELECTED_TRAVELING_CARD_ID, it).blockingAwait()
        }
    }
}
