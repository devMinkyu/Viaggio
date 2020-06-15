package com.kotlin.viaggio.view.traveling.enroll

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.kotlin.viaggio.R
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.*
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var transferUtility: TransferUtility
    @Inject
    lateinit var config: DeveloperAuthenticationProvider

    companion object{
        val TAG:String =  TravelingCardEnrollFragmentViewModel::class.java.toString()
    }

    private val writeCalendar = Calendar.getInstance()!!

    val complete: MutableLiveData<Event<Any>> = MutableLiveData()
    val imageLiveData:MutableLiveData<List<Any>> = MutableLiveData()
    val themeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()

    val contents = ObservableField<String>("").apply {
        addOnPropertyChangedCallback(object :androidx.databinding.Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    private val dayCount = ObservableInt(0)
    val mCountry = ObservableField<String>("")
    val dateOfTime = ObservableField<String>("")

    var travel = Travel()
    val imageList = mutableListOf<Bitmap>()
    val themeList = mutableListOf<String>()
    val isFormValid = ObservableBoolean(false)

    override fun initialize() {
        super.initialize()
        rxEventFunctional()
        imageLiveData.postValue(listOf())
        travelDataFetch()
    }

    private fun travelDataFetch() {
        val disposable = travelLocalModel.getTravel()
            .flatMap {
                travel = it
                writeCalendar.time = travel.startDate
                prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING)
            }
            .subscribe({
                val area = travel.area.first()
                mCountry.set("${area.country}_${area.city}")
                if (travel.travelKind == 2) {
                    dayCount.set(1)
                    dateOfTime.set(
                        SimpleDateFormat(
                            "a h:mm",
                            Locale.getDefault()
                        ).format(writeCalendar.time)
                    )
                } else {
                    if (it && prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID).blockingGet()
                        == prefUtilService.getLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID).blockingGet()
                    ) {
                        dayCount.set(prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet())
                        dateOfTime.set(
                            String.format(
                                resources.getString(R.string.travel_card_count),
                                dayCount.get()
                            )
                        )
                        mCountry.set(prefUtilService.getString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES).blockingGet())
                    } else {
                        dayCount.set(1)
                        dateOfTime.set(
                            String.format(
                                resources.getString(R.string.travel_card_count),
                                dayCount.get()
                            )
                        )
                    }
                }
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)
    }

    private fun rxEventFunctional() {
        var disposable = rxEventBus.travelCardImages
            .subscribeOn(Schedulers.io())
            .subscribe {
                imageLiveData.postValue(it)
                imageList.clear()
                imageList.addAll(it)
                validateForm()
            }
        addDisposable(disposable)
        disposable = rxEventBus.travelingOption.subscribe {
            when(it){
                is Area -> {
                    mCountry.set("${it.country}_${it.city}")
                }
                is List<*> ->{
                    val list = it.map { data ->
                        data as ThemeData
                        data.theme
                    }
                    themeList.clear()
                    themeList.addAll(list)
                    themeLiveData.value = Event(Any())
                }
                is Int ->{
                    dayCount.set(it)
                    dateOfTime.set(String.format(resources.getString(R.string.travel_card_count), dayCount.get()))
                }
            }
        }
        addDisposable(disposable)
        disposable = rxEventBus.travelingCalendar.subscribe {
            writeCalendar.time = it.time
        }
        addDisposable(disposable)
    }

    private fun validateForm() {
        when {
            TextUtils.isEmpty(contents.get()) && imageList.isNullOrEmpty() -> isFormValid.set(false)
            else -> isFormValid.set(true)
        }
    }
    fun permissionCheck(request: Observable<Boolean>?): LiveData<Boolean> {
        val imageViewShow: MutableLiveData<Boolean> = MutableLiveData()
        val disposable = request?.subscribe { t ->
            imageViewShow.value = t
        }
        disposable?.let { addDisposable(it) }
        return imageViewShow
    }

    fun saveCard(){
        val token = travelLocalModel.getToken()
        val mode = travelLocalModel.getUploadMode()
        val travelCard= TravelCard().apply {
            content = contents.get()!!
            localId = Calendar.getInstance().time.time
            travelLocalId = travel.localId
            travelServerId = travel.serverId
            date = Calendar.getInstance().time
            time = writeCalendar.time
            country = mCountry.get() ?: ""
            travelOfDay = dayCount.get()
            theme = themeList
        }
        val disposable = if (imageList.isNotEmpty()) {
            travelLocalModel.imagePathList(imageList)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable {
                    val completables = mutableListOf<Completable>()
                    travelCard.imageNames = it as ArrayList<String>
                    if (travel.localId != 0L && TextUtils.isEmpty(travel.imageName)) {
                        travel.imageName = it[0]
                        travel.userExist = false
                        val c1 = travelLocalModel.updateTravel(travel)
                            .andThen{co ->
                                if(TextUtils.isEmpty(token).not() && mode != 2 && travel.serverId != 0){
                                    updateWork(travel)
                                    co.onComplete()
                                } else {
                                    co.onComplete()
                                }
                            }
                        completables.add(c1)
                    }
                    completables.add(travelLocalModel.createTravelCard(travelCard))
                    Completable.merge(completables)
                }
        } else {
            travelLocalModel.createTravelCard(travelCard)
        }.andThen {
                if(TextUtils.isEmpty(token).not() && mode != 2 && travelCard.travelServerId != 0){
                    uploadWork(travelCard)
                    it.onComplete()
                } else {
                    it.onComplete()
                }
            }
            .observeOn(Schedulers.io())
            .subscribe {
                complete.postValue(Event(Any()))
                rxEventBus.travelCardUpdate.onNext(Any())
            }
        addDisposable(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        rxEventBus.travelCacheImages.onNext(listOf())
        rxEventBus.travelCardImages.onNext(listOf())
    }

    fun selectedCountry() {
        prefUtilService.putString(AndroidPrefUtilService.Key.SELECTED_COUNTRY, mCountry.get()!!).blockingAwait()
    }

    fun timeChange(hourOfDay: Int, minute: Int) {
        writeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        writeCalendar.set(Calendar.MINUTE, minute)
        dateOfTime.set(SimpleDateFormat("a h:mm", Locale.getDefault()).format(writeCalendar.time))
    }
}
