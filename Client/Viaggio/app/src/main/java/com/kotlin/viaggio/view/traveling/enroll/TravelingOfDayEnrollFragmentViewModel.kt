package com.kotlin.viaggio.view.traveling.enroll

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TravelingOfDayEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    val complete: MutableLiveData<Event<Any>> = MutableLiveData()
    val imageFirstLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
//    val changeCursor:MutableLiveData<Event<Any>> = MutableLiveData()

    val contents = ObservableField<String>("")
    val title = ObservableField<String>("")
    val date = ObservableField<String>("")

    var travelCard= TravelCard()
    var travelOfDay= TravelOfDay()
    var imageList = listOf<Bitmap>()

    override fun initialize() {
        super.initialize()
        val disposable = travelModel.getTravelCard()
            .observeOn(Schedulers.io())
            .subscribe({
                travelCard = it
                val date = SimpleDateFormat(appCtx.get().resources.getString(R.string.travel_of_day_pattern), Locale.ENGLISH).format(it.enrollOfTime).toUpperCase()
                this.date.set(date)
                this.contents.set(it.contents)
                this.title.set(it.title)
                if(it.imageNames.size > 0){
                    imageFirstLiveData.postValue(Event(it.imageNames[0]))
                }
            }){
            }
        addDisposable(disposable)

        val travelOfDayDisposable = travelModel.getTravelOfDay()
            .observeOn(Schedulers.io())
            .subscribe { t ->
                travelOfDay = t
                val date = SimpleDateFormat(appCtx.get().resources.getString(R.string.travel_of_day_pattern), Locale.ENGLISH).format(t.date).toUpperCase()
                this.date.set(date)
            }
        addDisposable(travelOfDayDisposable)

        val imageDisposable = rxEventBus.travelOfDayImages
            .subscribeOn(Schedulers.io())
            .subscribe {
                imageFirstLiveData.postValue(Event(it[0]))
                imageList = it
            }
        addDisposable(imageDisposable)
    }

    fun saveCard(){
        if(travelCard.id == 0L){
            travelCard.travelOfDayId = travelOfDay.id
            travelCard.contents = contents.get()!!
            travelCard.title = contents.get()!!
            travelCard.enrollOfTime = travelOfDay.date
            if (imageList.isNotEmpty()){
                val disposable = travelModel.imagePathList(imageList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMapCompletable {
                        travelCard.imageNames = it as ArrayList<String>
                        travelOfDay.themeImageName = it[0]
                        val completables = mutableListOf<Completable>()

                        val travelCardCompletable = travelModel.createTravelCard(travelCard)
                        val travelOfDayCompletable = travelModel.updateTravelOfDay(travelOfDay)

                        completables.add(travelCardCompletable)
                        completables.add(travelOfDayCompletable)
                        Completable.merge(completables)
                    }
                    .subscribe {
                        complete.postValue(Event(Any()))
                    }
                addDisposable(disposable)
            }else{
                val disposable = travelModel.createTravelCard(travelCard)
                    .observeOn(Schedulers.io())
                    .subscribe {
                        complete.postValue(Event(Any()))
                    }
                addDisposable(disposable)
            }
        }else{
            travelCard.contents = contents.get()!!
            travelCard.title = title.get()!!

            travelModel.updateTravelCard(travelCard)
            rxEventBus.travelCardUpdate.onNext(Any())
            complete.postValue(Event(Any()))
        }
    }

}
