package com.kotlin.viaggio.view.traveling

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.AppDatabase
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.tag_hive.saathi.saathi.error.InvalidFormException
import dagger.Lazy
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@SuppressLint("SimpleDateFormat")
class TravelingFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var gson: Gson

    val goToCamera: MutableLiveData<Event<Any>> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    val travelThemeListLiveData = MutableLiveData<Event<List<String>>>()

    private var travelThemeList:List<String> = listOf()

    lateinit var travelOfDayPagedLiveData: LiveData<PagedList<TravelOfDay>>

    val traveling = ObservableBoolean(false)
    val themeExist = ObservableBoolean(false)
    val travelingStartOfDay = ObservableField<String>("").apply {
        addOnPropertyChangedCallback(object :androidx.databinding.Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val travelingStartOfCountry = ObservableField<String>("").apply {
        addOnPropertyChangedCallback(object :androidx.databinding.Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val isFormValid = ObservableBoolean(false)
    private var validateFormDisposable: Disposable? = null

    private fun validateForm() {
        validateFormDisposable?.dispose()
        validateFormDisposable = Maybe
            .create<Any> {
                when {
                    TextUtils.isEmpty(travelingStartOfDay.get()) -> throw InvalidFormException()
                    TextUtils.isEmpty(travelingStartOfCountry.get()) -> throw InvalidFormException()
                    travelThemeList.isNullOrEmpty() -> throw InvalidFormException()
                    else -> it.onSuccess(Any())
                }
            }.map {
                isFormValid.set(true)
            }
            .onErrorComplete {
                isFormValid.set(false)
                it is InvalidFormException
            }.subscribe()
        validateFormDisposable?.let {
            addDisposable(it)
        }
    }
    override fun initialize() {
        super.initialize()
        traveling.set(prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet())
        loadTravelingOfDay()
        if(!traveling.get()){
            val themeDisposable = rxEventBus.travelOfTheme
                .subscribe { t ->
                    travelThemeList = t
                    validateForm()
                    travelThemeListLiveData.postValue(Event(t))
                    if(t.isNotEmpty()){
                        themeExist.set(true)
                    }
                }
            addDisposable(themeDisposable)

            val countryDisposable = rxEventBus.travelOfCountry.subscribe { t ->
                travelingStartOfCountry.set(t)
            }
            addDisposable(countryDisposable)

            val cal = Calendar.getInstance()
            travelingStartOfDay.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).format(cal.time))
        }

        val disposable = rxEventBus.travelOfDayChange
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it){
                    loadTravelingOfDay()
                    rxEventBus.travelOfDayChange.onNext(false)
                }
            }){

            }
        addDisposable(disposable)
    }
    private fun loadTravelingOfDay(){
        val factory: DataSource.Factory<Int, TravelOfDay>
                = travelModel.getTravelOfDays()
        val pagedListBuilder: LivePagedListBuilder<Int, TravelOfDay> = LivePagedListBuilder<Int, TravelOfDay>(factory,
            20)
        travelOfDayPagedLiveData = pagedListBuilder.build()
    }
    fun permissionCheck(request: Observable<Boolean>?) {
        val disposable = request?.subscribe { t ->
            if(t){
                goToCamera.value = Event(Any())
            }else{
                permissionRequestMsg.value = Event(PermissionError.NECESSARY_PERMISSION)
            }
        }
        disposable?.let { addDisposable(it) }
    }

    fun travelStart() {
        traveling.set(true)
        val cal = Calendar.getInstance()
        val d1 = prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, true).observeOn(Schedulers.io()).subscribe()
        val d2 = prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 1).observeOn(Schedulers.io()).subscribe()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        val d3 = prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).observeOn(Schedulers.io()).subscribe()

        val travel = Travel(
            countries = arrayListOf(travelingStartOfCountry.get()!!),
            startOfDay = SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).parse(travelingStartOfDay.get()!!),
            userId = prefUtilService.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet(),
            theme = travelThemeList.toMutableList() as ArrayList<String>
        )
        val travelOfDay = TravelOfDay(countries = arrayListOf(travelingStartOfCountry.get()!!),
            day = SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).parse(travelingStartOfDay.get()!!))
        val d4= prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, travelingStartOfCountry.get()!!).observeOn(Schedulers.io()).subscribe()
        val disposable = travelModel.createTravel(travel)
            .flatMap {
                travelOfDay.travelId = it
                val d5= prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_ID, it).observeOn(Schedulers.io()).subscribe()
                addDisposable(d5)
                travelModel.createTravelOfDay(travelOfDay)
            }
            .subscribe { t ->
                val d6 = prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ID, t).observeOn(Schedulers.io()).subscribe()
                addDisposable(d6)
                travelOfDay.travelId = t
            }
        addDisposable(disposable)
        addDisposable(d1)
        addDisposable(d2)
        addDisposable(d3)
        addDisposable(d4)
    }
}
