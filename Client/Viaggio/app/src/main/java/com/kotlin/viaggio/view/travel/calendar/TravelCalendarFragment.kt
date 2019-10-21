package com.kotlin.viaggio.view.travel.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.databinding.FragmentTravelCalendarBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.widget.daysOfWeekFromLocale
import com.kotlin.viaggio.widget.makeInVisible
import com.kotlin.viaggio.widget.makeVisible
import com.kotlin.viaggio.widget.setTextColorRes
import kotlinx.android.synthetic.main.calendar_day_legend.*
import kotlinx.android.synthetic.main.fragment_travel_calendar.*
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import kotlinx.android.synthetic.main.item_calendar_header.view.*
import kotlinx.coroutines.*
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.design.snackbar
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*


class TravelCalendarFragment : BaseFragment<TravelCalendarFragmentViewModel>() {
    private val today = LocalDate.now()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().enrollMode = it.getBoolean(ArgName.TRAVEL_CALENDAR.name, false)
            getViewModel().option = it.getBoolean(ArgName.MODIFY_CALENDART.name, false)
        }
    }

    companion object {
        val TAG: String = TravelCalendarFragment::class.java.simpleName
    }

    lateinit var binding: FragmentTravelCalendarBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_travel_calendar, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the First day of week depending on Locale
        val daysOfWeek = daysOfWeekFromLocale()
        legendLayout.childrenRecursiveSequence().forEachIndexed { index, view1 ->
            (view1 as TextView).apply {
                text = daysOfWeek[index].name.take(3).toLowerCase(Locale.getDefault()).capitalize()
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTextColorRes(R.color.greyish_brown)
            }
        }

        val currentMonth = YearMonth.now()
        exFourCalendar.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(2),
            daysOfWeek.first()
        )
        exFourCalendar.scrollToMonth(currentMonth)


        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.exFourDayText
            val roundBgView = view.exFourRoundBgView

            init {
                view.setOnClickListener {
                    if(day.date.isAfter(today)) {
                        return@setOnClickListener
                    }
                    val date = day.date
                    if (getViewModel().travelKind == 2 || getViewModel().enrollMode || getViewModel().traveling) {
                        getViewModel().startDate = date
                        getViewModel().endDate = date
                        exFourCalendar.notifyCalendarChanged()
                        getViewModel().bindSummaryViews()
                    } else {
                        if (getViewModel().startDate != null) {
                            if (date < getViewModel().startDate || getViewModel().endDate != null) {
                                getViewModel().startDate = date
                                getViewModel().endDate = null
                            } else if (date != getViewModel().startDate) {
                                getViewModel().endDate = date
                            }
                        } else {
                            getViewModel().startDate = date
                        }
                        exFourCalendar.notifyCalendarChanged()
                        getViewModel().bindSummaryViews()
                    }
                }
            }
        }
        exFourCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(
                container: DayViewContainer,
                day: CalendarDay
            ) {
                container.day = day
                val textView = container.textView
                val roundBgView = container.roundBgView

                textView.text = null
                textView.background = null
                roundBgView.makeInVisible()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.text = day.day.toString()
                    if (day.date.isAfter(today)) {
                        textView.setTextColorRes(R.color.light_grey)
                    } else {
                        calendarDayTextColor(day, textView, roundBgView)
                    }
                }
            }

            fun calendarDayTextColor(day:CalendarDay, textView:TextView, roundBgView:View) {
                when {
                    getViewModel().startDate == day.date && getViewModel().endDate == day.date -> {
                        textView.setTextColorRes(R.color.white)
                        roundBgView.makeVisible()
                        roundBgView.setBackgroundResource(R.drawable.calendar_single_selected_bg)
                    }
                    getViewModel().startDate == day.date && getViewModel().endDate == null -> {
                        textView.setTextColorRes(R.color.white)
                        roundBgView.makeVisible()
                        roundBgView.setBackgroundResource(R.drawable.calendar_single_selected_bg)
                    }
                    day.date == getViewModel().startDate -> {
                        textView.setTextColorRes(R.color.white)
                        getViewModel().updateDrawableRadius(textView)
                        textView.background = getViewModel().startBackground
                    }
                    getViewModel().startDate != null && getViewModel().endDate != null && (day.date > getViewModel().startDate && day.date < getViewModel().endDate) -> {
                        textView.setTextColorRes(R.color.white)
                        textView.setBackgroundResource(R.drawable.calendar_continuous_selected_bg_middle)
                    }
                    day.date == getViewModel().endDate -> {
                        textView.setTextColorRes(R.color.white)
                        getViewModel().updateDrawableRadius(textView)
                        textView.background = getViewModel().endBackground
                    }
                    day.date == today -> {
                        textView.setTextColorRes(R.color.colorAccent)
                        roundBgView.makeVisible()
                        roundBgView.setBackgroundResource(R.drawable.calendar_today_bg)
                    }
                    else -> textView.setTextColorRes(R.color.greyish_brown)
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = view.exFourHeaderText
        }
        exFourCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthTitle =
                    "${month.yearMonth.month.name.toLowerCase(Locale.getDefault()).capitalize()} ${month.year}"
                container.textView.text = monthTitle
            }
        }

        getViewModel().updateView.observe(this, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                        delay(200)
                    }

                    getViewModel().startDate = convertLocalDate(getViewModel().travel!!.startDate)
                    if(getViewModel().travel!!.endDate == null) {
                        getViewModel().travel!!.endDate = getViewModel().travel!!.startDate
                    }
                    getViewModel().endDate = convertLocalDate(getViewModel().travel!!.endDate)
                    exFourCalendar.notifyCalendarChanged()
                    getViewModel().bindSummaryViews()
                }
            }
        })
    }

    private fun convertDate(date: LocalDate): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.monthValue - 1)
        calendar.set(Calendar.DATE, date.dayOfMonth)
        return calendar.time
    }
    private fun convertLocalDate(date: Date?): LocalDate? {
        return date?.let {
            LocalDate.parse(
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(date),
                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
            )
        }
    }

    inner class ViewHandler {
        fun close() {
            fragmentPopStack()
        }
        fun clear() {
            getViewModel().startDate = null
            getViewModel().endDate = null
            exFourCalendar.notifyCalendarChanged()
            getViewModel().bindSummaryViews()
        }

        fun confirm() {
            if(getViewModel().option) {
                showLoading()
                val startDate = convertDate(getViewModel().startDate!!)
                val endDate = convertDate(getViewModel().endDate!!)
                getViewModel().modifyCalendar(startDate, endDate).observe(this@TravelCalendarFragment, androidx.lifecycle.Observer {
                    stopLoading()
                    if(it) {
                        parentFragmentManager.popBackStack()
                    } else {
                        view?.snackbar("error")
                        parentFragmentManager.popBackStack()
                    }
                })
            } else {
                if (getViewModel().enrollMode) {
                    val startDate = convertDate(getViewModel().startDate!!)
                    getViewModel().selectedDate(startDate)
                } else {
                    if (getViewModel().startDate != null && getViewModel().endDate != null) {
                        val startDate = convertDate(getViewModel().startDate!!)
                        val endDate = convertDate(getViewModel().endDate!!)
                        getViewModel().travelTerm(startDate, endDate)
                    }
                    baseIntent("http://viaggio.kotlin.com/traveling/enroll/")
                }
                parentFragmentManager.popBackStackImmediate()
            }

        }
    }
}
