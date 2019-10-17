package com.kotlin.viaggio.view.travel.calendar

import android.content.Context
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_RANGE
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_SINGLE
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_travel_calendar.*
import java.util.*


class TravelCalendarFragment : BaseFragment<TravelCalendarFragmentViewModel>() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().option = it.getBoolean(ArgName.TRAVEL_CALENDAR.name, false)
        }
    }

    companion object {
        val TAG:String = TravelCalendarFragment::class.java.simpleName
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelCalendarBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_calendar, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(travelingCalendarContainer, SlidrConfig.Builder().position(
                SlidrPosition.BOTTOM)
                .build())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(getViewModel().travelKind == 2) {
            calendarView.selectionMode = SELECTION_MODE_SINGLE
        } else {
            calendarView.selectionMode = SELECTION_MODE_RANGE
        }

        calendarView.state().edit()
            .setMinimumDate(CalendarDay.from(2010, 1, 1))
            .setMaximumDate(CalendarDay.today())
            .commit()
        calendarView.addDecorator(SundayDecorator())

        calendarView.setOnRangeSelectedListener { _, dates ->
            getViewModel().list.clear()
            getViewModel().list.addAll(dates)
        }
        calendarView.setOnDateChangedListener { _, date, selected ->
            if(getViewModel().travelKind == 2) {
                getViewModel().list.clear()
                getViewModel().list.add(date)
            } else {
                if(selected && getViewModel().option){
                    val startDate = convertDate(date)
                    getViewModel().selectedDate(startDate)
                    fragmentPopStack()
                }else{
                    getViewModel().list.clear()
                }
            }
        }
    }

    fun convertDate(date:CalendarDay): Date{
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.month - 1)
        calendar.set(Calendar.DATE, date.day)
        return calendar.time
    }

    inner class ViewHandler{
        fun back() {
            fragmentPopStack()
        }
        fun confirm(){
            val startDate = convertDate(getViewModel().list.first())
            val endDate = convertDate(getViewModel().list.last())
            getViewModel().travelTerm(startDate, endDate)

            baseIntent("http://viaggio.kotlin.com/traveling/enroll/")
            parentFragmentManager.popBackStackImmediate()
        }
    }
    inner class SundayDecorator:DayViewDecorator{
        val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            day?.let {
                calendar.set(Calendar.YEAR, day.year)
                calendar.set(Calendar.MONTH, day.month - 1)
                calendar.set(Calendar.DATE, day.day)
            }
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.light_red, null)))
        }
    }
}
