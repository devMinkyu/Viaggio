package com.kotlin.viaggio.view.travel.option

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.UpdateTravelWorker
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class TravelingRepresentativeImageFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val choose: MutableList<ObservableBoolean> = mutableListOf()
    val list:MutableList<String> = mutableListOf()

    val imageNamesListLiveDate:MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val completeLiveDate:MutableLiveData<Event<Any>> = MutableLiveData()

    var chooseIndex:Int = 0
    override fun initialize() {
        super.initialize()
        val disposable = travelLocalModel.getTravelCards()
            .subscribeOn(Schedulers.io())
            .subscribe({travelCards ->
                for (travelCard in travelCards) {
                    for (imageName in travelCard.imageNames) {
                        list.add(imageName)
                        choose.add(ObservableBoolean(false))
                    }
                }
                imageNamesListLiveDate.postValue(Event(list))
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }

    fun changeRepresentative() {
        val index = chooseIndex
        val imageName = list[index]
        var travel = Travel()

        val disposable = travelLocalModel.getTravel()
            .flatMapCompletable {
                travel = it
                travel.imageName = imageName
                travel.userExist = false
                travelLocalModel.updateTravel(travel)
            }.andThen {
                val token = travelLocalModel.getToken()
                val mode = travelLocalModel.getUploadMode()
                if (TextUtils.isEmpty(token).not() && mode != 2 && travel.serverId != 0) {
                    updateWork(travel)
                    it.onComplete()
                } else {
                    it.onComplete()
                }
            }
            .subscribe({
                rxEventBus.travelUpdate.onNext(Any())
                completeLiveDate.postValue(Event(Any()))
            }){

            }
        addDisposable(disposable = disposable)
    }
}
