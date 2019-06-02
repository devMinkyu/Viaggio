package com.kotlin.viaggio.view.theme

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.Theme
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
    val addLiveData: MutableLiveData<Event<Any>> = MutableLiveData()
    val removeLiveData: MutableLiveData<Event<Int>> = MutableLiveData()

    val customTheme: ObservableField<String> = ObservableField("")
    val selectedTheme: ObservableArrayList<ThemeData> = ObservableArrayList()

    var option = false
    var travel = Travel()
    var themeList: MutableList<ThemeData> = mutableListOf()
    override fun initialize() {
        super.initialize()
        val disposable = themeModel.getThemes()
            .subscribe({ themes ->
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

    private fun settingTheme(list: List<ThemeData>) {
        themeList = list.toMutableList()
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
        } else {
            rxEventBus.travelOfTheme
                .subscribe { t ->
                    if (option.not()) {
                        t.map { selected ->
                            val item = list.first {
                                selected.theme == it.theme
                            }
                            item.select.set(true)
                            if (selectedTheme.contains(item).not()) {
                                selectedTheme.add(item)
                            }
                        }
                        themesListLiveData.postValue(Event(list))
                    }
                }
        }
        addDisposable(selectedDisposable)
    }

    fun createCustomTheme() {
        if (TextUtils.isEmpty(customTheme.get()).not()) {
            val item = Theme(theme = "# ${customTheme.get()}", authority = true)
            customTheme.set("")
            val disposable = themeModel.createTheme(item)
                .subscribe({
                    val result = ThemeData(theme = item.theme, authority = item.authority)
                    themeList.add(result)
                    addLiveData.postValue(Event(Any()))
                }) {
                    Timber.d(it)
                }
            addDisposable(disposable)
        }
    }

    fun removeCustomTheme(data: ThemeData, index: Int) {
        val item = Theme(theme = data.theme, authority = data.authority)
        val disposable = themeModel.deleteTheme(item)
            .subscribe({
                themeList.remove(data)
                removeLiveData.postValue(Event(index))
                if (selectedTheme.contains(data)) {
                    selectedTheme.remove(data)
                }
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)
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
            completeLiveData.value = Event(Any())
            rxEventBus.travelOfTheme.onNext(selectedTheme)
        }
    }
}