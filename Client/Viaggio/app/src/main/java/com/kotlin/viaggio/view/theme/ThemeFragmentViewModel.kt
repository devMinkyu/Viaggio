package com.kotlin.viaggio.view.theme

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.reflect.TypeToken
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.obj.Theme
import com.kotlin.viaggio.data.obj.ThemeData
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.UpdateTravelWorker
import timber.log.Timber
import java.io.InputStreamReader
import javax.inject.Inject

class ThemeFragmentViewModel @Inject constructor() : BaseViewModel() {
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