package com.kotlin.viaggio.view.traveling.enroll

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val complete: MutableLiveData<Event<Any>> = MutableLiveData()
    val imageLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    val imageViewShow: MutableLiveData<Event<Any>> = MutableLiveData()

    val contents = ObservableField<String>("")
    val title = ObservableField<String>("")
    val date = ObservableField<String>("")

    var travelCard= TravelCard()
    var travelOfDay= TravelOfDay()
    var imageList = listOf<Bitmap>()

    override fun initialize() {
        super.initialize()
        val imageDisposable = rxEventBus.travelOfDayImages
            .subscribeOn(Schedulers.io())
            .subscribe {
                imageLiveData.postValue(Event(it[0]))
                imageList = it
            }
        addDisposable(imageDisposable)
    }

    fun permissionCheck(request: Observable<Boolean>?) {
        val disposable = request?.subscribe { t ->
            if (t) {
                imageViewShow.value = Event(Any())
            } else {
                permissionRequestMsg.value = Event(PermissionError.STORAGE_PERMISSION)
            }
        }
        disposable?.let { addDisposable(it) }
    }

    fun saveCard(){
        if(travelCard.id == 0L){
            travelCard.travelId = travelOfDay.id
            travelCard.content = contents.get()!!
            travelCard.date = travelOfDay.date
            if (imageList.isNotEmpty()){
                val disposable = travelLocalModel.imagePathList(imageList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMapCompletable {
                        travelCard.imageNames = it as ArrayList<String>
                        travelOfDay.themeImageName = it[0]
                        val completables = mutableListOf<Completable>()

                        val travelCardCompletable = travelLocalModel.createTravelCard(travelCard)
                        val travelOfDayCompletable = travelLocalModel.updateTravelOfDay(travelOfDay)

                        completables.add(travelCardCompletable)
                        completables.add(travelOfDayCompletable)
                        Completable.merge(completables)
                    }
                    .subscribe {
                        complete.postValue(Event(Any()))
                    }
                addDisposable(disposable)
            }else{
                val disposable = travelLocalModel.createTravelCard(travelCard)
                    .observeOn(Schedulers.io())
                    .subscribe {
                        complete.postValue(Event(Any()))
                    }
                addDisposable(disposable)
            }
        }else{
            travelCard.content = contents.get()!!

            travelLocalModel.updateTravelCard(travelCard)
            rxEventBus.travelCardUpdate.onNext(Any())
            complete.postValue(Event(Any()))
        }
    }

}
