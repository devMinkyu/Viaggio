package com.kotlin.viaggio.view.traveling.enroll

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.tag_hive.saathi.saathi.error.InvalidFormException
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val complete: MutableLiveData<Event<Any>> = MutableLiveData()
    val imageLiveData:MutableLiveData<Event<List<Any>>> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    val imageViewShow: MutableLiveData<Event<Any>> = MutableLiveData()

    val contents = ObservableField<String>("").apply {
        addOnPropertyChangedCallback(object :androidx.databinding.Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val dayCount = ObservableInt(0)
    val country = ObservableField<String>("")

    var travelCard= TravelCard()
    var travel = Travel()
    var imageList = listOf<Bitmap>()
    val isFormValid = ObservableBoolean(false)

    override fun initialize() {
        super.initialize()
        imageLiveData.postValue(Event(listOf()))
        val imageDisposable = rxEventBus.travelCardImages
            .subscribeOn(Schedulers.io())
            .subscribe {
                imageLiveData.postValue(Event(it))
                imageList = it
                validateForm()
            }
        addDisposable(imageDisposable)

        val disposable = travelLocalModel.getTravel()
            .subscribe({
                travel = it
            }){
                Timber.d(it)
            }
        addDisposable(disposable)

        dayCount.set(prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet())
        country.set(prefUtilService.getString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES).blockingGet())
    }

    private fun validateForm() {
        when {
            TextUtils.isEmpty(contents.get()) && imageList.isNullOrEmpty() -> isFormValid.set(false)
            else -> isFormValid.set(true)
        }
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
            val travelId = prefUtilService.getLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID).blockingGet()
            travelCard.travelId = travelId
            travelCard.content = contents.get()!!
            travelCard.date = Calendar.getInstance().time
            travelCard.country = country.get()?:""
            travelCard.travelOfDay = dayCount.get()
            if (imageList.isNotEmpty()){
                val disposable = travelLocalModel.imagePathList(imageList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMapCompletable {
                        travelCard.imageNames = it as ArrayList<String>

                        val completables = mutableListOf<Completable>()
                        val travelCardCompletable = travelLocalModel.createTravelCard(travelCard)
                        if(travel.id != 0L && TextUtils.isEmpty(travel.imageName)){
                            travel.imageName = it[0]
                            val travelCompletable = travelLocalModel.updateTravel(travel)
                            completables.add(travelCompletable)
                        }
                        completables.add(travelCardCompletable)
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
