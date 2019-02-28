package com.kotlin.viaggio.view.home

import android.util.Log
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class HomeMainFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    override fun initialize() {
        super.initialize()

        val a = travelModel.getTravelCards().subscribe { t1, t2 ->
            Log.d("hoho", "$t1")
        }
        addDisposable(a)
    }
}
