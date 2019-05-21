package com.kotlin.viaggio.view.traveling.option

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.ThemeData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class TravelingThemesActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()
    val themesListLiveData: MutableLiveData<Event<Any>> = MutableLiveData()

    val themeList = mutableListOf<ThemeData>()
    val chooseThemesList = ObservableArrayList<ThemeData>()
    override fun initialize() {
        super.initialize()

        val disposable = travelLocalModel.getTravel()
            .subscribeOn(Schedulers.io())
            .subscribe ({
                val list = it.theme.map {themeVal ->
                        ThemeData(theme = themeVal)
                    }
                themeList.addAll(list)
                themesListLiveData.postValue(Event(Any()))
            }){
                Timber.d(it)
            }
        addDisposable(disposable = disposable)
    }

    fun confirm() {
        rxEventBus.travelingOption.onNext(chooseThemesList)
        completeLiveDate.value = Event(Any())
    }
}