package com.kotlin.viaggio.view.traveling.detail

import androidx.databinding.ObservableField
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TravelingDetailFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val travelingOfDayCount:ObservableField<String> = ObservableField("")
    val travelingOfDay:ObservableField<String> = ObservableField("")
    val travelingOfDayTheme:ObservableField<String> = ObservableField("")

    override fun initialize() {
        super.initialize()
        val disposable = travelModel.getTravelOfDay()
            .observeOn(Schedulers.io())
            .subscribe ({

            }){

            }
        addDisposable(disposable)
    }
}
