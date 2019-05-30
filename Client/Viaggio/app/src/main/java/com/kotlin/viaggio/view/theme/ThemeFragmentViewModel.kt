package com.kotlin.viaggio.view.theme

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.ThemeData
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.ThemeModel
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import timber.log.Timber
import javax.inject.Inject

class ThemeFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var themeModel: ThemeModel

    val themesListLiveData: MutableLiveData<Event<List<ThemeData>>> = MutableLiveData()
    val completeLiveData: MutableLiveData<Event<Any>> = MutableLiveData()

    val selectedTheme: ObservableArrayList<ThemeData> = ObservableArrayList()

    var option = false
    var travel = Travel()
    override fun initialize() {
        super.initialize()
        val disposable = themeModel.getThemes()
            .subscribe({themes ->
                val list = themes.map {
                    ThemeData(theme = it.theme, authority = it.authority)
                }
                themesListLiveData.postValue(Event(list))
                settingTheme(list)
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)

    }
    private fun settingTheme(list:List<ThemeData>){
        val selectedDisposable = if (option) {
            travelLocalModel.getTravel()
                .subscribe({
                    travel = it
                    it.theme.map { themeVal ->
                        val item = list.first {
                            themeVal == it.theme
                        }
                        item.select.set(true)
                        if (selectedTheme.contains(item).not()) {
                            selectedTheme.add(item)
                        }
                    }
                    themesListLiveData.postValue(Event(list))
                }) {
                    Timber.d(it)
                }
        } else {rxEventBus.travelOfTheme
            .subscribe { t ->
                t.map { selected ->
                    val item = list.first {
                        selected.theme == it.theme
                    }
                    item.select.set(true)
                    if (selectedTheme.contains(item).not()) {
                        selectedTheme.add(item)
                    }
                }
                if(option.not()){
                    themesListLiveData.value = Event(list)
                }
            }
        }
        addDisposable(selectedDisposable)
    }

    fun sendTheme() {
        if (option) {
            if (travel.localId != 0L) {
                travel.theme = selectedTheme.map { it.theme }.toMutableList()
                travel.userExist = false
                val disposable = travelLocalModel.updateTravel(travel)
                    .andThen {
                        val token = travelLocalModel.getToken()
                        val mode = travelLocalModel.getUploadMode()
                        if (TextUtils.isEmpty(token).not() && mode != 2 && travel.serverId != 0) {
                            updateWork(travel)
                            it.onComplete()
                        } else {
                            it.onComplete()
                        }
                    }
                    .subscribe {
                        completeLiveData.postValue(Event(Any()))
                    }
                addDisposable(disposable)
            }
        } else {
            option = true
            rxEventBus.travelOfTheme.onNext(selectedTheme)
            completeLiveData.value = Event(Any())
        }
    }
}