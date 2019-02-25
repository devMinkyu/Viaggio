package com.kotlin.viaggio.view.theme

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.data.`object`.Theme
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class ThemeFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var gson: Gson

    val themesList:MutableLiveData<Event<Theme>> = MutableLiveData()

    val themes = Theme(mutableListOf())
    override fun initialize() {
        super.initialize()
        val inputStream = InputStreamReader(appCtx.get().assets.open("theme.json"))
        val themes: Theme = gson.fromJson(inputStream, Theme::class.java)
        themesList.value = Event(themes)
    }

    fun chooseTheme(theme: String){
        themes.themes.add(theme)
    }
    fun cancelTheme(theme: String){
        themes.themes.remove(theme)
    }
}
