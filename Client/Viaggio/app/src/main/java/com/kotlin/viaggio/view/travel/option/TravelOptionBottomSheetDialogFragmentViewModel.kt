package com.kotlin.viaggio.view.travel.option

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import javax.inject.Inject


class TravelOptionBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val isExistTravelCard = ObservableBoolean(false)
    var travel = Travel()

    val resultLiveData = MutableLiveData<Event<Boolean>>()
    val showLoadingLiveData = MutableLiveData<Event<Any>>()
    override fun initialize() {
        super.initialize()
        val disposable = travelLocalModel.getTravel()
            .subscribe({
                travel = it
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
        val travelCardDisposable = travelLocalModel.getTravelCards()
            .subscribe({
                isExistTravelCard.set(it.isNotEmpty())
            }){
                Timber.d(it)
            }
        addDisposable(travelCardDisposable)

        val travelDeleteDisposable = rxEventBus.travelDelete
            .subscribe {
                showLoadingLiveData.value = Event(Any())
                travelDelete()
            }
        addDisposable(travelDeleteDisposable)
    }

    private fun travelDelete(){
        var list = listOf<TravelCard>()
        travel.isDelete = true
        val disposable = travelLocalModel.getTravelCards()
            .flatMapCompletable { travelCards ->
                list = travelCards.map {
                    it.imageNames.map { fileName ->
                        File(fileName).delete()
                    }
                    it.isDelete = true
                    it
                }
                val completable = mutableListOf<Completable>()
                completable.add(travelLocalModel.updateTravel(travel))
                completable.add(travelLocalModel.updateTravelCards(list))
                Completable.merge(completable)
            }
            .andThen {
                val token = travelLocalModel.getToken()
                val mode = travelLocalModel.getUploadMode()

                if (TextUtils.isEmpty(token).not() && mode != 2 && travel.serverId != 0) {
                    updateWork(travel)
                    list.map {travelCard ->
                        if(travelCard.serverId != 0) {
                            updateWork(travelCard)
                        }
                    }
                    it.onComplete()
                } else {
                    it.onComplete()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                rxEventBus.travelUpdate.onNext(Any())
                resultLiveData.postValue(Event(true))
            }) {
                resultLiveData.postValue(Event(false))
                Timber.d(it)
            }
        addDisposable(disposable)
    }
}