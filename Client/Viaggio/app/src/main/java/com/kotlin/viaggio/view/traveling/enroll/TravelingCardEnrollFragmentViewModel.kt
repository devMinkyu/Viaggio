package com.kotlin.viaggio.view.traveling.enroll

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.google.gson.Gson
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.`object`.*
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.UploadTravelWorker
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class TravelingCardEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var transferUtility: TransferUtility
    @Inject
    lateinit var config: DeveloperAuthenticationProvider

    val complete: MutableLiveData<Event<Any>> = MutableLiveData()
    val imageLiveData:MutableLiveData<Event<List<Any>>> = MutableLiveData()
    val themeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
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
    val imageList = mutableListOf<Bitmap>()
    val themeList = mutableListOf<String>()
    val isFormValid = ObservableBoolean(false)

    override fun initialize() {
        super.initialize()
        imageLiveData.postValue(Event(listOf()))
        val imageDisposable = rxEventBus.travelCardImages
            .subscribeOn(Schedulers.io())
            .subscribe {
                imageLiveData.postValue(Event(it))
                imageList.clear()
                imageList.addAll(it)
                validateForm()
            }
        addDisposable(imageDisposable)

        val disposable = travelLocalModel.getTravel()
            .subscribe({
                travel = it
                dayCount.set(1)
                val area = it.area.first()
                country.set("${area.country}_${area.city}")
            }){
                Timber.d(it)
            }
        addDisposable(disposable)

        if(prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()){
            if(prefUtilService.getLong(AndroidPrefUtilService.Key.TRAVELING_ID).blockingGet() == prefUtilService.getLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID).blockingGet()){
                dayCount.set(prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet())
                country.set(prefUtilService.getString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES).blockingGet())
            }
        }

        val optionDisposable = rxEventBus.travelingOption.subscribe {
            when(it){
                is Area -> {
                    country.set("${it.country}_${it.city}")
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
                }
            }
        }
        addDisposable(optionDisposable)
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
        travelCard.content = contents.get()!!
        val travelId = prefUtilService.getLong(AndroidPrefUtilService.Key.SELECT_TRAVEL_ID).blockingGet()
        travelCard.travelId = travelId
        travelCard.date = Calendar.getInstance().time
        travelCard.country = country.get() ?: ""
        travelCard.travelOfDay = dayCount.get()
        travelCard.theme = themeList
        val disposable = if (imageList.isNotEmpty()) {
            travelLocalModel.imagePathList(imageList)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    travelCard.imageNames = it as ArrayList<String>
                    if (travel.id != 0L && TextUtils.isEmpty(travel.imageName)) {
                        travel.imageName = it[0]
                        travel.userExist = false
                        travelLocalModel.updateTravel(travel).subscribe()
                    }
                    travelLocalModel.createTravelCard(travelCard)
                }
        } else {
            travelLocalModel.createTravelCard(travelCard)
        }.flatMapCompletable {
                travelCard.id = it
                val token = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()
                val mode = prefUtilService.getInt(AndroidPrefUtilService.Key.UPLOAD_MODE).blockingGet()
                if(TextUtils.isEmpty(token).not() && mode != 2){
                    Completable.fromAction {
                        val constraints =
                            if (mode == 0) {
                                Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()
                            } else {
                                Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .setRequiresCharging(true)
                                    .build()
                            }

                        val resultJsonTravelCard = gson.toJson(travelCard)

                        val data = Data.Builder()
                            .putString(WorkerName.TRAVEL_CARD.name, resultJsonTravelCard)
                            .build()
                        val travelWork = OneTimeWorkRequestBuilder<UploadTravelWorker>()
                            .setConstraints(constraints)
                            .setInputData(data)
                            .build()

                        WorkManager.getInstance().enqueue(travelWork)
                    }
                }else{
                    Completable.complete()
                }
            }
            .observeOn(Schedulers.io())
            .subscribe {
                config.setInfo("ap-northeast-2:c7d4b95e-8611-495e-a182-0ee815896b0b", "eyJraWQiOiJhcC1ub3J0aGVhc3QtMjEiLCJ0eXAiOiJKV1MiLCJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJhcC1ub3J0aGVhc3QtMjpjN2Q0Yjk1ZS04NjExLTQ5NWUtYTE4Mi0wZWU4MTU4OTZiMGIiLCJhdWQiOiJhcC1ub3J0aGVhc3QtMjozZjM3YmFiMy1lZjI5LTQ4YTktOTBmZS04YTIyMmRhMTg1OGMiLCJhbXIiOlsiYXV0aGVudGljYXRlZCIsImxvZ2luLnZpYWdnaW8udmlhZ2dpbyIsImxvZ2luLnZpYWdnaW8udmlhZ2dpbzphcC1ub3J0aGVhc3QtMjozZjM3YmFiMy1lZjI5LTQ4YTktOTBmZS04YTIyMmRhMTg1OGM6MSJdLCJpc3MiOiJodHRwczovL2NvZ25pdG8taWRlbnRpdHkuYW1hem9uYXdzLmNvbSIsImV4cCI6MTU1NzM2MTg0MCwiaWF0IjoxNTU3Mjc1NDQwfQ.KIpWsFh06GJ7kHTYb8cwSjB5yvnyeCmG2c45KepZ28tn4XC1KsPaNQK5hYBj14QzybWEc44o1M3j6myPqKAFtFkzY0t63NeFAxHCwMj0FpVJtCout6UTRBru-99XzgWmUZyRRkbmpt4msGgTWmZzbGzouAzSRklD8VaZO1n54OyiIBE6vGuGJIw1Ktl2jL2FBtS7EF6mqO-fPju8tbPKnanjlM_e0ejZUpUki9S_Wv2CTol4sMkydYJTAQvSFyezXt-F7225Ccu_CcToeofuBylKl8p9dzvK5owJI7VMVDnSRGBNB_-i-WGzYhV7d3qDSmpZOFjtoOijo1dZAyJzHw")
                val uploadObserver = transferUtility.upload(BuildConfig.S3_UPLOAD_BUCKET, "image/${travelCard.imageNames.first().split("/").last()}", File(travelCard.imageNames.first()))
                uploadObserver.setTransferListener(object : TransferListener {
                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                    override fun onStateChanged(id: Int, state: TransferState?) {
                        if(state == TransferState.COMPLETED){
                            complete.postValue(Event(Any()))
                            rxEventBus.travelCardUpdate.onNext(Any())
                        }
                    }
                    override fun onError(id: Int, ex: Exception?) {
                    }
                })
            }
        addDisposable(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        rxEventBus.travelCacheImages.onNext(listOf())
        rxEventBus.travelCardImages.onNext(listOf())
    }

    fun selectedCountry() {
        prefUtilService.putString(AndroidPrefUtilService.Key.SELECTED_COUNTRY, country.get()!!).blockingAwait()
    }
}
