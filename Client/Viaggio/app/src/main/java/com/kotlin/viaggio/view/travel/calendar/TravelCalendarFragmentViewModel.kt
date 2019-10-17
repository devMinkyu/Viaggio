package com.kotlin.viaggio.view.travel.calendar

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.*
import javax.inject.Inject


class TravelCalendarFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val completeLiveData = MutableLiveData<Event<Any>>()
    val list = ObservableArrayList<CalendarDay>()

    var option = false

    var travelKind = 0
    override fun initialize() {
        super.initialize()
        travelKind = prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVEL_KINDS).blockingGet()
    }

    fun selectedDate(startTime: Date){
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime))
    }
    fun travelTerm(startTime: Date, endTime: Date) {
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime, endTime))
    }
}
