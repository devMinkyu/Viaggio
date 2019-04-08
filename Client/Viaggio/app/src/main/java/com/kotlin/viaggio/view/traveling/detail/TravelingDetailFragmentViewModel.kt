package com.kotlin.viaggio.view.traveling.detail

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TravelingDetailFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val title = ObservableField<String>("")
    val content = ObservableField<String>("")
    val date = ObservableField<String>("")

    val travelOfDayCardImageListLiveData = MutableLiveData<Event<List<String>>>()

    override fun initialize() {
        super.initialize()
        fetchData()
        val disposable = rxEventBus.travelCardUpdate
            .subscribe {
                fetchData()
            }
        addDisposable(disposable)
    }

    private fun fetchData(){
        val disposable = travelLocalModel.getTravelCard()
            .observeOn(Schedulers.io())
            .subscribe { t ->
                travelOfDayCardImageListLiveData.postValue(Event(t.imageNames))
                content.set(t.content)
                date.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.travel_of_day_pattern), Locale.ENGLISH).format(t.date).toUpperCase())
            }
        addDisposable(disposable)
    }
}
