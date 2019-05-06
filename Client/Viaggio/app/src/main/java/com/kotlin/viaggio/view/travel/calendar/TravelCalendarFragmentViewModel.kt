package com.kotlin.viaggio.view.travel.calendar

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.*
import javax.inject.Inject


class TravelCalendarFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson

    val completeLiveData = MutableLiveData<Event<Any>>()
    val list = ObservableArrayList<CalendarDay>()

    var option = false
    fun selectedDate(startTime: Date){
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime))
    }
    fun travelTerm(startTime: Date, endTime: Date) {
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime, endTime))
    }
}
