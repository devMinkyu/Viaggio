package com.kotlin.viaggio.view.traveling.option

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.ThemeData
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
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

    var changeMode = false
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

    fun change() {
        var travelCard = TravelCard()
        val disposable = travelLocalModel.getTravelCard()
            .flatMapCompletable {list ->
                list.firstOrNull()?.let {travelCardVal ->
                    travelCardVal.theme = chooseThemesList.map { it.theme }.toMutableList()
                    travelCardVal.userExist = false
                    travelCard = travelCardVal
                    travelLocalModel.updateTravelCard(travelCardVal)
                }?: Completable.complete()
            }
            .andThen {
                if(travelCard.localId != 0L) {
                    val token = travelLocalModel.getToken()
                    val mode = travelLocalModel.getUploadMode()
                    if (TextUtils.isEmpty(token).not() && mode != 2 && travelCard.serverId != 0) {
                        updateWork(travelCard)
                        it.onComplete()
                    } else {
                        it.onComplete()
                    }
                } else {
                    it.onComplete()
                }
            }
            .subscribe({
                rxEventBus.travelCardUpdate.onNext(Any())
                completeLiveDate.postValue(Event(Any()))
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }
    fun confirm() {
        rxEventBus.travelingOption.onNext(chooseThemesList)
        completeLiveDate.value = Event(Any())
    }
}