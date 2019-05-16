package com.kotlin.viaggio.view.travel.option

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject


class TravelTitleBottomSheetDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val confirmLiveData = MutableLiveData<Event<Any>>()
    val changeCursorLiveData = MutableLiveData<Event<Any>>()
    var isChangeCursor = false
    val travelTitle = ObservableField<String>("").apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if(isChangeCursor.not()){
                    changeCursorLiveData.postValue(Event(Any()))
                }
            }
        })
    }
    var travel = Travel()
    override fun initialize() {
        super.initialize()

        val disposable = travelLocalModel.getTravel()
            .subscribe({
                travel = it
                travelTitle.set(it.title)
            }){

            }
        addDisposable(disposable)
    }

    fun confirm() {
        if(travel.localId != 0L){
            travel.title = travelTitle.get()!!
            travel.userExist = false
            val disposable = travelLocalModel.updateTravel(travel)
                .subscribe {
                    rxEventBus.travelUpdate.onNext(Any())
                    confirmLiveData.postValue(Event(Any()))
                }
            addDisposable(disposable)
        }
    }
}