package com.kotlin.viaggio.view.traveling.detail

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.ArrayList
import javax.inject.Inject

class TravelingDetailFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    lateinit var travelOfDay:TravelOfDay

    val travelingOfDayCount:ObservableInt = ObservableInt(0)
    val travelingOfDay:ObservableField<String> = ObservableField("")
    val travelingOfDayTheme:ObservableField<String> = ObservableField("")
    val isTheme:ObservableBoolean = ObservableBoolean(false)

    @SuppressLint("SimpleDateFormat")
    override fun initialize() {
        super.initialize()
        val disposable = travelModel.getTravelOfDay()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .flatMap {
                travelOfDay = it
                travelingOfDayCount.set(it.travelOfDay)
                travelingOfDay.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).format(it.date))
                if(travelOfDay.theme.isNotEmpty()){
                    isTheme.set(true)
                    travelingOfDayTheme.set("")
                    for (s in travelOfDay.theme) {
                        travelingOfDayTheme.set("${travelingOfDayTheme.get()} $s")
                        travelingOfDayTheme.set(travelingOfDayTheme.get()?.trim())
                    }
                    travelModel.updateTravelOfDay(travelOfDay)
                }
                Single.just(travelOfDay)
            }
            .subscribe ({travelOfDay ->
                val disposable = rxEventBus.travelOfDayTheme
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe{
                        travelOfDay.theme = it as ArrayList<String>
                        if(travelOfDay.theme.isNotEmpty()){
                            isTheme.set(true)
                            travelingOfDayTheme.set("")
                            for (s in travelOfDay.theme) {
                                travelingOfDayTheme.set("${travelingOfDayTheme.get()} $s")
                                travelingOfDayTheme.set(travelingOfDayTheme.get()?.trim())
                            }
                            travelModel.updateTravelOfDay(travelOfDay)
                        }
                    }
                addDisposable(disposable)
            }){

            }
        addDisposable(disposable)
    }
}
