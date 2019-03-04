package com.kotlin.viaggio.view.traveling.detail

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TravelingRepresentativeImageFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel


    val imageNamesListLiveDate:MutableLiveData<Event<MutableList<TravelCard>>> = MutableLiveData()
    override fun initialize() {
        super.initialize()
        val disposable = travelModel.getTravelCards()
            .subscribeOn(Schedulers.io())
            .subscribe({
                imageNamesListLiveDate.postValue(Event(it))
            }){

            }
        addDisposable(disposable)
    }

    fun saveRepresentativeImage(imageName:String){

    }

}
