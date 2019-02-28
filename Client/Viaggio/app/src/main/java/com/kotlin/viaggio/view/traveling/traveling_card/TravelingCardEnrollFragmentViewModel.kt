package com.kotlin.viaggio.view.traveling.traveling_card

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val imagePathList: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val complete:MutableLiveData<Event<Any>> = MutableLiveData()

    val chooseCountList:MutableList<ObservableInt> = mutableListOf()
    var entireChooseCount:Int = 1
    val imageAllList:MutableList<String> = mutableListOf()
    val imageChooseList:MutableList<String> = mutableListOf()

    val contents = ObservableField<String>("")
    val additional = ObservableBoolean(false)
    val place = ObservableField<String>("")
    val time = ObservableField<String>("")
    val transportation = ObservableField<String>("")
    val overImageCount = ObservableInt(0)

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

        time.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.dateTimeFormat)).format(Date(System.currentTimeMillis())))

        val disposable = rxEventBus.travelCardTransportation
            .observeOn(Schedulers.io())
            .subscribe({
                transportation.set(it)
            }){

            }
        addDisposable(disposable)
    }

    fun checkAdditional() =
        when{
            TextUtils.isEmpty(place.get()) -> false
            TextUtils.isEmpty(time.get()) -> false
            TextUtils.isEmpty(transportation.get()) -> false
            else -> { true }
        }

    fun saveTravelCard() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, time.get()!!.split(":")[0].toInt())
        cal.set(Calendar.MINUTE, time.get()!!.split(":")[1].toInt())

        val order = prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ORDER).blockingGet()
        val travelCard = TravelCard(country = prefUtilService.getString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES).blockingGet(),
            contents = contents.get()!!, travelCardPlace = place.get()!!, enrollOfTime = cal.time,
            travelOfDayId = prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ID).blockingGet(), previousTransportation = arrayListOf(transportation.get()!!),
            order = order
        )

        val disposable = travelModel.imagePathList(imageChooseList)
            .flatMapCompletable {
                val imageNames:MutableList<String> = mutableListOf()
                for (s in it) {
                    imageNames.add(Uri.parse(s).lastPathSegment!!)
                }
                travelCard.imageNames = imageNames as ArrayList<String>
                travelModel.createTravelCard(travelCard).andThen {
                    if(order == 1){
                        travelModel.getTravelOfDay()
                            .flatMapCompletable {travelOfCard ->
                                travelOfCard.themeImageName = imageNames[0]
                                travelModel.updateTravelOfDay(travelOfCard)
                            }
                    }else{
                        Completable.complete()
                    }
                }
            }
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                complete.postValue(Event(Any()))
                rxEventBus.travelCardUpdate.onNext(Any())
            }){

            }
        addDisposable(disposable)
    }

}
