package com.kotlin.viaggio.view.travel.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.widget.getDrawableCompat
import com.kotlin.viaggio.widget.setCornerRadius
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


class TravelCalendarFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val completeLiveData = MutableLiveData<Event<Any>>()

    var option = false

    var travelKind = 0

    val exFourStartDateText = ObservableField("")
    val exFourStartDateColor = ObservableField<ColorStateList>()
    val exFourEndDateText = ObservableField("")
    val exFourEndDateColor = ObservableField<ColorStateList>()
    val exFourSaveButton = ObservableBoolean(false)

    var startDate: LocalDate? = null
    var endDate: LocalDate? = null
    private val headerDateFormatter = DateTimeFormatter.ofPattern("EEE'\n'MMM d")
    private val mContext:Context by lazy {
        return@lazy appCtx.get()
    }
    val startBackground: GradientDrawable by lazy {
        val mDrawable = mContext.getDrawableCompat(R.drawable.calendar_continuous_selected_bg_start)
            ?: ResourcesCompat.getDrawable(resources, R.drawable.calendar_continuous_selected_bg_start, null)
        return@lazy mDrawable as GradientDrawable
    }

    val endBackground: GradientDrawable by lazy {
        val mDrawable = mContext.getDrawableCompat(R.drawable.calendar_continuous_selected_bg_end)
            ?: ResourcesCompat.getDrawable(resources, R.drawable.calendar_continuous_selected_bg_end, null)
        return@lazy mDrawable as GradientDrawable
    }
    override fun initialize() {
        super.initialize()
        travelKind = prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVEL_KINDS).blockingGet()
        bindSummaryViews()
    }
    private var radiusUpdated = false

    fun updateDrawableRadius(textView: TextView) {
        if (radiusUpdated) return
        radiusUpdated = true
        val radius = (textView.height / 2).toFloat()
        startBackground.setCornerRadius(topLeft = radius, bottomLeft = radius)
        endBackground.setCornerRadius(topRight = radius, bottomRight = radius)
    }

    fun bindSummaryViews() {
        if (startDate != null) {
            exFourStartDateText.set(headerDateFormatter.format(startDate))
            exFourStartDateColor.set(mContext.getColorStateList(R.color.greyish_brown))
        } else {
            exFourStartDateText.set(resources.getString(R.string.start_date))
            exFourStartDateColor.set(mContext.getColorStateList(R.color.light_grey))
        }
        if(option) {
            exFourEndDateText.set(resources.getString(R.string.travel_no_end))
            exFourEndDateColor.set(mContext.getColorStateList(R.color.light_grey))
        } else {
            if (endDate != null) {
                exFourEndDateText.set(headerDateFormatter.format(endDate))
                exFourEndDateColor.set(mContext.getColorStateList(R.color.greyish_brown))
            } else {
                exFourEndDateText.set(resources.getString(R.string.end_date))
                exFourEndDateColor.set(mContext.getColorStateList(R.color.light_grey))
            }
        }

        // Enable save button if a range is selected or no date is selected at all, Airbnb style.
        exFourSaveButton.set(endDate != null || (startDate == null && endDate == null))
    }

    fun selectedDate(startTime: Date){
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime))
    }
    fun travelTerm(startTime: Date, endTime: Date) {
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime, endTime))
    }
}
