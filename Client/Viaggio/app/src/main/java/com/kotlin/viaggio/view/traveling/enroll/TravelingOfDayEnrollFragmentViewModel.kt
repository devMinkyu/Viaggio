package com.kotlin.viaggio.view.traveling.enroll

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TravelingOfDayEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val complete: MutableLiveData<Event<Any>> = MutableLiveData()
    val changeCursor:MutableLiveData<Event<Any>> = MutableLiveData()

    val contents = ObservableField<String>("").apply {
        addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                changeCursor.value = Event(Any())
            }
        })
    }
    val title = ObservableField<String>("")
    val date = ObservableField<String>("")

    var travelOfDay = TravelOfDay()


    override fun initialize() {
        super.initialize()
        val cal = Calendar.getInstance()
        val date = SimpleDateFormat("EEEE / MMMM d / yyyy", Locale.ENGLISH).format(cal.time).toUpperCase()
        this.date.set(date)
    }

}
