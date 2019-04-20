package com.kotlin.viaggio.view.theme

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Theme
import com.kotlin.viaggio.data.`object`.ThemeData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class ThemeFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var gson: Gson

    val themesListLiveData: MutableLiveData<Event<List<ThemeData>>> = MutableLiveData()

    val selectedTheme: ObservableArrayList<ThemeData> = ObservableArrayList()
    override fun initialize() {
        super.initialize()
        val inputStream =
            InputStreamReader(appCtx.get().assets.open(appCtx.get().resources.getString(R.string.travel_theme_json)))
        val type = object : TypeToken<List<Theme>>() {}.type

        val themes: List<Theme> = gson.fromJson(inputStream, type)

        val list = themes.map {
            ThemeData(theme = it)
        }
        themesListLiveData.value = Event(list)

        val disposable = rxEventBus.travelOfTheme
            .subscribe { t ->
                t.map {selected ->
                    val item = list.first {
                        selected.theme == it.theme
                    }
                    item.select.set(true)
                }
                themesListLiveData.value = Event(list)
            }
        addDisposable(disposable)
    }

    fun sendTheme() {
        rxEventBus.travelOfTheme.onNext(selectedTheme)
    }
}


