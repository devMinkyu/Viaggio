package com.kotlin.viaggio.view.theme

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Theme
import com.kotlin.viaggio.data.`object`.ThemeData
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import timber.log.Timber
import java.io.InputStreamReader
import javax.inject.Inject

class ThemeFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val themesListLiveData: MutableLiveData<Event<List<ThemeData>>> = MutableLiveData()
    val completeLiveData: MutableLiveData<Event<Any>> = MutableLiveData()

    val selectedTheme: ObservableArrayList<ThemeData> = ObservableArrayList()

    var option = false
    var travel = Travel()
    override fun initialize() {
        super.initialize()
        val inputStream =
            InputStreamReader(appCtx.get().assets.open(appCtx.get().resources.getString(R.string.travel_theme_json)))
        val type = object : TypeToken<List<Theme>>() {}.type

        val themes: List<Theme> = gson.fromJson(inputStream, type)

        val list = themes.map {
            ThemeData(theme = it.theme, authority = it.authority)
        }
        themesListLiveData.value = Event(list)

        if (option) {
            val disposable = travelLocalModel.getTravel()
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
            addDisposable(disposable)
        } else {
            val disposable = rxEventBus.travelOfTheme
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
            addDisposable(disposable)
        }

    }

    fun sendTheme() {
        if (option) {
            if (travel.id != 0L) {
                travel.theme = selectedTheme.map { it.theme }.toMutableList()
                travel.userExist = false
                val disposable = travelLocalModel.updateTravel(travel)
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