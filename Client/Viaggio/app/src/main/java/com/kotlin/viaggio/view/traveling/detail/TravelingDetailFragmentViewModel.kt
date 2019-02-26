package com.kotlin.viaggio.view.traveling.detail

import android.annotation.SuppressLint
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.kotlin.viaggio.R
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import javax.inject.Inject

class TravelingDetailFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val travelingOfDayCount:ObservableInt = ObservableInt(0)
    val travelingOfDay:ObservableField<String> = ObservableField("")
    val travelingOfDayTheme:ObservableField<String> = ObservableField("")

    @SuppressLint("SimpleDateFormat")
    override fun initialize() {
        super.initialize()
        val disposable = travelModel.getTravelOfDay()
            .subscribeOn(Schedulers.io())
            .subscribe ({
                travelingOfDayCount.set(it.dayCount)
                travelingOfDay.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).format(it.day))
            }){

            }
        addDisposable(disposable)
    }
}
