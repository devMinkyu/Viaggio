package com.kotlin.viaggio.view.traveling

import android.util.Log
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.CountryList
import com.kotlin.viaggio.data.`object`.TutorialList
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class TravelingCountryFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var gson: Gson
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open(appCtx.get().getString(R.string.travel_country_json)))
        val countries: CountryList = gson.fromJson(inputStream, CountryList::class.java)

        Log.d("hoho", "$countries")
    }
}
