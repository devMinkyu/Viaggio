package com.kotlin.viaggio.view.theme

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Theme
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class ThemeFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var gson: Gson

    val themesListLiveData: MutableLiveData<Event<Theme>> = MutableLiveData()
    val showSelectTheme:MutableLiveData<Event<Any>> = MutableLiveData()

    val themesList = Theme()
    val themes = Theme()
    override fun initialize() {
        super.initialize()
        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().resources.getString(R.string.travel_theme_json)))
        val themes: Theme = gson.fromJson(inputStream, Theme::class.java)
        themesListLiveData.value = Event(themes)
        themesList.themes.addAll(themes.themes)

        val disposable = rxEventBus.travelOfTheme.subscribe { t ->
            this.themes.themes = t.toMutableList()
            showSelectTheme.value = Event(Any())
        }
        addDisposable(disposable)
    }

    fun chooseTheme(theme: String) {
        themes.themes.add(theme)
    }

    fun cancelTheme(theme: String) {
        themes.themes.remove(theme)
    }

    fun sendTheme() {
        rxEventBus.travelOfTheme.onNext(themes.themes)
    }
}
