package com.kotlin.viaggio.view.traveling.traveling_card

import android.annotation.SuppressLint
import android.net.Uri
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val imagePathList: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val complete: MutableLiveData<Event<Any>> = MutableLiveData()

    val chooseCountList: MutableList<ObservableInt> = mutableListOf()
    var entireChooseCount: Int = 1
    val imageAllList: MutableList<String> = mutableListOf()
    val imageChooseList: MutableList<String> = mutableListOf()

    val contents = ObservableField<String>("")
    val additional = ObservableBoolean(false)
    val place = ObservableField<String>("")
    val time = ObservableField<String>("")
    val transportation = ObservableField<String>("")
    val overImageCount = ObservableInt(0)
    val severalCountries = ObservableBoolean(false)
    val country1 = ObservableField<String>("")
    val country2 = ObservableField<String>("")

    var travelOfDay = TravelOfDay()
    var selectedCountry = ""
    @SuppressLint("SimpleDateFormat")
    override fun initialize() {
        super.initialize()
        for (s in travelModel.imageAllPath()) {
            imageAllList.add(s)
            chooseCountList.add(ObservableInt(0))
        }
        imageChooseList.add(imageAllList[0])
        chooseCountList[0].set(1)
        imagePathList.value = Event(imageAllList)

        time.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.date_time_format)).format(Date(System.currentTimeMillis())))

        val disposable = rxEventBus.travelCardTransportation
            .observeOn(Schedulers.io())
            .subscribe({
                transportation.set(it)
            }) {

            }
        addDisposable(disposable)

        val travelOfDayDisposable = travelModel.getTravelOfDay()
            .subscribeOn(Schedulers.io())
            .subscribe({
                travelOfDay = it
                severalCountries.set(it.dayCountries.size > 1)
                selectedCountry = it.dayCountries[0]

                for ((i,dayCountry) in it.dayCountries.withIndex()) {
                    when(i){
                        0 -> country1.set(dayCountry)
                        1 -> country2.set(dayCountry)
                    }
                }
            }){

            }
        addDisposable(travelOfDayDisposable)
    }

    fun saveTravelCard() {
        val imagePathSingle = travelModel.imagePathList(imageChooseList)
        val orderTravelCardSingle = travelModel.getTravelCards()

        val disposable = Single.zip(
            imagePathSingle,
            orderTravelCardSingle,
            BiFunction<List<String>, List<TravelCard>, TravelCard>  { t1, t2 ->
                val imageNames: MutableList<String> = mutableListOf()
                for (s in t1) {
                    imageNames.add(Uri.parse(s).lastPathSegment!!)
                }
                val order = if (t2.isNullOrEmpty()) 1 else t2.size + 1

                val cal = Calendar.getInstance()
                cal.time = travelOfDay.date
                cal.set(Calendar.HOUR_OF_DAY, time.get()!!.split(":")[0].toInt())
                cal.set(Calendar.MINUTE, time.get()!!.split(":")[1].toInt())
                val travelCard = TravelCard(
                    country = selectedCountry,
                    contents = contents.get()!!,
                    travelCardPlace = place.get()!!,
                    enrollOfTime = cal.time,
                    travelOfDayId = prefUtilService.getLong(AndroidPrefUtilService.Key.SELECTED_TRAVELING_OF_DAY_ID).blockingGet(),
                    previousTransportation = arrayListOf(transportation.get()!!),
                    order = order,
                    imageNames = imageNames as ArrayList<String>
                )
                if (order == 1) {
                    travelOfDay.themeImageName = imageNames[0]
                    travelModel.updateTravelOfDay(travelOfDay)
                    rxEventBus.travelOfDayImage.onNext(travelOfDay.themeImageName)
                }
                travelCard
            }).flatMap { travelCard ->
            travelModel.createTravelCard(travelCard)
        }
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                complete.postValue(Event(Any()))
                rxEventBus.travelCardUpdate.onNext(Any())
            }) {

            }
        addDisposable(disposable)

    }

}
