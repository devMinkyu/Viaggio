package com.kotlin.viaggio.view.travel.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.widget.getDrawableCompat
import com.kotlin.viaggio.widget.setCornerRadius
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TravelCalendarFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var travelModel: TravelModel

    var enrollMode = false
    var option = false
    var traveling = false

    var travelKind = 0

    val exFourStartDateText = ObservableField("")
    val exFourStartDateColor = ObservableField<ColorStateList>()
    val exFourEndDateText = ObservableField("")
    val exFourEndDateColor = ObservableField<ColorStateList>()
    val exFourSaveButton = ObservableBoolean(false)

    var startDate: LocalDate? = null
    var endDate: LocalDate? = null

    var travel: Travel? = null
    private val headerDateFormatter = DateTimeFormatter.ofPattern("EEE'\n'MMM d")
    private val mContext: Context by lazy {
        return@lazy appCtx.get()
    }
    val startBackground: GradientDrawable by lazy {
        val mDrawable = mContext.getDrawableCompat(R.drawable.calendar_continuous_selected_bg_start)
            ?: ResourcesCompat.getDrawable(
                resources,
                R.drawable.calendar_continuous_selected_bg_start,
                null
            )
        return@lazy mDrawable as GradientDrawable
    }

    val endBackground: GradientDrawable by lazy {
        val mDrawable = mContext.getDrawableCompat(R.drawable.calendar_continuous_selected_bg_end)
            ?: ResourcesCompat.getDrawable(
                resources,
                R.drawable.calendar_continuous_selected_bg_end,
                null
            )
        return@lazy mDrawable as GradientDrawable
    }

    val updateView = MutableLiveData<Event<Any>>()
    override fun initialize() {
        super.initialize()
        exFourStartDateText.set(resources.getString(R.string.start_date))
        exFourStartDateColor.set(mContext.getColorStateList(R.color.light_grey))
        exFourEndDateText.set(resources.getString(R.string.end_date))
        exFourEndDateColor.set(mContext.getColorStateList(R.color.light_grey))
        if (option) {
            traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
            val disposable = travelLocalModel.getTravel()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    travel = it
                    travelKind = it.travelKind
                    bindSummaryViews()
                    updateView.value = Event(Any())
                }) {
                    Timber.d(it)
                }
            addDisposable(disposable)
        } else {
            travelKind =
                prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVEL_KINDS).blockingGet()
            bindSummaryViews()
        }
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
        if (enrollMode || traveling) {
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
        exFourSaveButton.set(endDate != null)
    }

    fun selectedDate(startTime: Date) {
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime))
    }

    fun travelTerm(startTime: Date, endTime: Date) {
        rxEventBus.travelingStartOfDay.onNext(listOf(startTime, endTime))
    }

    fun modifyCalendar(startDate: Date, endDate: Date): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        if(travel == null) {
            result.value = false
        }
        travel?.let { mTravel ->
            val mEndDate:Date? = if(traveling) null else endDate
            mTravel.startDate = startDate
            mTravel.endDate = mEndDate
            mTravel.userExist = false

            val disposable = Completable.mergeArray(updateTravelCard(startDate), travelLocalModel.updateTravel(mTravel))
                .subscribeOn(Schedulers.io())
                .andThen {
                    val token = travelLocalModel.getToken()
                    val mode = travelLocalModel.getUploadMode()
                    if (TextUtils.isEmpty(token).not() && mode != 2 && mTravel.serverId != 0) {
                        updateWork(mTravel)
                        it.onComplete()
                    } else {
                        it.onComplete()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    rxEventBus.travelUpdate.onNext(Any())
                    result.value = true
                }) {
                    Timber.d(it)
                    result.value = false
                }
            addDisposable(disposable)
        }
        return result
    }

    fun updateTravelCard(startDate: Date): Completable {
        var result: List<TravelCard>
        return travelLocalModel.getTravelCards()
            .flatMapCompletable {
                result = it.map {travelCard ->
                    val calendar = Calendar.getInstance()
                    calendar.time = startDate
                    calendar.add(Calendar.DAY_OF_MONTH, travelCard.travelOfDay - 1)
                    travelCard.time = calendar.time
                    travelCard
                }
                val completableList = mutableListOf<Completable>()
                completableList.add(travelLocalModel.updateTravelCards(result))

                val token = travelLocalModel.getToken()
                val mode = travelLocalModel.getUploadMode()
                val list =result.filter { travelCard ->
                    travelCard.serverId != 0
                }
                if (TextUtils.isEmpty(token).not() && mode != 2 && list.isNotEmpty()) {
                    completableList.add(travelModel.updateSyncTravelCards(list))
                }
                Completable.merge(completableList)
            }
    }
}
