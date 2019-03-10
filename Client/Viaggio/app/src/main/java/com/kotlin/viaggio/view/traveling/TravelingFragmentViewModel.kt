package com.kotlin.viaggio.view.traveling

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.`object`.TravelingError
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.TimeCheckWorker
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@SuppressLint("SimpleDateFormat")
class TravelingFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var gson: Gson

    val goToCamera: MutableLiveData<Event<Any>> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    val errorMsg: MutableLiveData<Event<TravelingError>> = MutableLiveData()
    val travelThemeListLiveData = MutableLiveData<Event<List<String>>>()
    val travelOfDayListLiveData:MutableLiveData<MutableList<TravelOfDay>> = MutableLiveData()

    private var travelThemeList:List<String> = listOf()


    var isFabOpen = false

    val isClick:ObservableBoolean = ObservableBoolean(false)
    val traveling = ObservableBoolean(false)
    val themeExist = ObservableBoolean(false)
    val travelingStartOfDay = ObservableField<String>("")
    val travelingStartOfCountry = ObservableField<String>("")

    override fun initialize() {
        super.initialize()
        traveling.set(prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet())
        if(!traveling.get()){
            val cal = Calendar.getInstance()
            travelingStartOfDay.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).format(cal.time))

            val themeDisposable = rxEventBus.travelOfTheme
                .subscribe { t ->
                    travelThemeList = t
                    travelThemeListLiveData.postValue(Event(t))
                    if(t.isNotEmpty()){
                        themeExist.set(true)
                    }
                }
            addDisposable(themeDisposable)
            val travelingStartOfDayDisposable = rxEventBus.travelingStartOfDay
                .subscribe({
                    if(TextUtils.isEmpty(it).not()){
                        if(traveling.get().not()){
                            travelingStartOfDay.set(it)
                        }else{
                            rxEventBus.travelingStartOfDay.onNext("")
                        }
                    }
                }){

                }
            addDisposable(travelingStartOfDayDisposable)
        }else{
            loadTravelOfDayPaged()
        }
        val disposable = rxEventBus.travelOfDayChange
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it){
                    loadTravelOfDayPaged()
                    rxEventBus.travelOfDayChange.onNext(false)
                }
            }){

            }
        addDisposable(disposable)
        val travelingFinishDisposable = rxEventBus.travelFinish
            .subscribe({
                if(it){
                    traveling.set(it.not())
                    travelingStartOfCountry.set("")
                    val cal = Calendar.getInstance()
                    travelingStartOfDay.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).format(cal.time))

                    rxEventBus.travelFinish.onNext(it.not())
                }
            }){

            }
        addDisposable(travelingFinishDisposable)
        val countryDisposable = rxEventBus.travelOfCountry.subscribe { t ->
            if(traveling.get()){
                loadTravelOfDayPaged()
            }else{
                travelingStartOfCountry.set(t)
            }
        }
        addDisposable(countryDisposable)
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

    private fun loadTravelOfDayPaged(){
        val disposable = travelModel.getTravelOfDays()
            .subscribeOn(Schedulers.io())
            .subscribe { t ->
                travelOfDayListLiveData.postValue(t)
            }
        addDisposable(disposable)
    }

    fun click(){
        isClick.set(!isClick.get())
    }

    fun changeStartOfDay(startOfDay: String) {
        travelingStartOfDay.set(startOfDay)
        rxEventBus.travelingStartOfDay.onNext(startOfDay)
    }

    fun setSelectedTravelingOfDay(travelOfDayId: Long?) {
        travelOfDayId?.let {
            val disposable
                    = prefUtilService.putLong(AndroidPrefUtilService.Key.SELECTED_TRAVELING_OF_DAY_ID, it)
                .observeOn(Schedulers.io()).subscribe()
            addDisposable(disposable)
        }
    }

    fun travelStart():Boolean {
        when{
            TextUtils.isEmpty(travelingStartOfCountry.get()) ->{
                errorMsg.value = Event(TravelingError.COUNTRY_EMPTY)
                return false
            }
            themeExist.get().not() -> {
                errorMsg.value = Event(TravelingError.THEME_EMPTY)
                return false
            }
        }

        traveling.set(true)
        val cal = Calendar.getInstance()
        prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, true).observeOn(Schedulers.io()).blockingAwait()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).observeOn(Schedulers.io()).blockingAwait()

        val travel = Travel(
            entireCountries = arrayListOf(travelingStartOfCountry.get()!!),
            startDate = SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).parse(travelingStartOfDay.get()!!),
            userId = prefUtilService.getInt(AndroidPrefUtilService.Key.USER_ID).blockingGet(),
            theme = travelThemeList.toMutableList() as ArrayList<String>
        )
        prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, travelingStartOfCountry.get()!!).observeOn(Schedulers.io()).blockingAwait()

        val disposable = travelModel.createTravel(travel)
            .flatMap {
                prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_ID, it).observeOn(Schedulers.io()).blockingAwait()
                val day = Math.floor(((cal.time.time - SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).parse(travelingStartOfDay.get()!!).time).toDouble()/1000)/(24*60*60)).toInt()
                val result = mutableListOf<TravelOfDay>()
                cal.time = SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).parse(travelingStartOfDay.get()!!)
                for(index in 0 .. day){
                    if(index != 0) {
                        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth + 1)
                    }
                    val travelOfDay = TravelOfDay(dayCountries = arrayListOf(travelingStartOfCountry.get()!!), travelOfDay = index+1,
                        date = cal.time, travelId = it)
                    result.add(travelOfDay)
                }
                prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, day+1).observeOn(Schedulers.io()).blockingAwait()
                travelModel.createTravelOfDays(result)
            }
            .subscribe { t ->
                prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ID, t.last()).observeOn(Schedulers.io()).blockingAwait()

                loadTravelOfDayPaged()
            }
        addDisposable(disposable)
        val timeCheckWork = PeriodicWorkRequestBuilder<TimeCheckWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(WorkerName.TRAVELING_OF_DAY_CHECK.name,ExistingPeriodicWorkPolicy.KEEP,timeCheckWork)
        return true
    }
}
