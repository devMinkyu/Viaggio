package com.kotlin.viaggio.view.travel.option

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
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

            }
        addDisposable(disposable)
    }

    fun changeRepresentative() {
        val index = chooseIndex
        val imageName = list[index]

        val disposable = travelLocalModel.getTravel()
            .flatMapCompletable {
                it.imageName = imageName
                it.userExist = false
                travelLocalModel.updateTravel(it)
            }
            .subscribe({
                rxEventBus.travelUpdate.onNext(Any())
                completeLiveDate.postValue(Event(Any()))
            }){

            }
        addDisposable(disposable = disposable)
    }

}
