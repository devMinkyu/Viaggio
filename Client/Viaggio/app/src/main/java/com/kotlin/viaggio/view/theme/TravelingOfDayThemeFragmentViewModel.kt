package com.kotlin.viaggio.view.theme

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Theme
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class TravelingOfDayThemeFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var gson: Gson

    val themesList: MutableLiveData<Event<Theme>> = MutableLiveData()
    val complete:MutableLiveData<Event<Any>> = MutableLiveData()

    val themes = Theme(mutableListOf())
    override fun initialize() {
        super.initialize()
        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().resources.getString(R.string.travel_theme_json)))
        val themes: Theme = gson.fromJson(inputStream, Theme::class.java)
        themesList.value = Event(themes)
    }
    fun cancelTheme(theme: String) {
        themes.themes.remove(theme)
    }

    fun sendTheme(theme: String) {
        themes.themes.add(theme)
        if(themes.themes.size == 2){
            rxEventBus.travelOfDayTheme.onNext(themes.themes)
            complete.value = Event(Any())
        }
    }
}
