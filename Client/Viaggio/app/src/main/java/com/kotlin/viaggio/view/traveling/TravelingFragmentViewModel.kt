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
import com.tag_hive.saathi.saathi.error.InvalidFormException
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
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
    val errortMsg: MutableLiveData<Event<TravelingError>> = MutableLiveData()
    val travelThemeListLiveData = MutableLiveData<Event<List<String>>>()

    private var travelThemeList:List<String> = listOf()

    lateinit var travelOfDayPagedLiveData: LiveData<PagedList<TravelOfDay>>

    var isFabOpen = false

    val isClick:ObservableBoolean = ObservableBoolean(false)
    val traveling = ObservableBoolean(false)
    val themeExist = ObservableBoolean(false)
    val travelingStartOfDay = ObservableField<String>("")
    val travelingStartOfCountry = ObservableField<String>("")

    override fun initialize() {
        super.initialize()
        traveling.set(prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet())
        loadTravelingOfDay()
        if(!traveling.get()){
            val themeDisposable = rxEventBus.travelOfTheme
                .subscribe { t ->
                    travelThemeList = t
                    travelThemeListLiveData.postValue(Event(t))
                    if(t.isNotEmpty()){
                        themeExist.set(true)
                    }
                }
            addDisposable(themeDisposable)

            val countryDisposable = rxEventBus.travelOfCountry.subscribe { t ->
                if(traveling.get()){
                    loadTravelingOfDay()
                }else{
                    travelingStartOfCountry.set(t)
                }
            }
            addDisposable(countryDisposable)

            val cal = Calendar.getInstance()
            travelingStartOfDay.set(SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).format(cal.time))
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
        when{
            TextUtils.isEmpty(travelingStartOfCountry.get()) ->{
                errortMsg.value = Event(TravelingError.COUNTRY_EMPTY)
                return
            }
            themeExist.get().not() -> {
                errortMsg.value = Event(TravelingError.THEME_EMPTY)
                return
            }
        }

        traveling.set(true)
        val cal = Calendar.getInstance()
        prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, true).observeOn(Schedulers.io()).blockingAwait()
        prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 1).observeOn(Schedulers.io()).blockingAwait()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).observeOn(Schedulers.io()).blockingAwait()

        val travel = Travel(
            entireCountries = arrayListOf(travelingStartOfCountry.get()!!),
            startDate = SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).parse(travelingStartOfDay.get()!!),
            userId = prefUtilService.getInt(AndroidPrefUtilService.Key.USER_ID).blockingGet(),
            theme = travelThemeList.toMutableList() as ArrayList<String>
        )
        val travelOfDay = TravelOfDay(dayCountries = arrayListOf(travelingStartOfCountry.get()!!),
            date = SimpleDateFormat(appCtx.get().resources.getString(R.string.date_format)).parse(travelingStartOfDay.get()!!))
        prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, travelingStartOfCountry.get()!!).observeOn(Schedulers.io()).blockingAwait()

        val disposable = travelModel.createTravel(travel)
            .flatMap {
                travelOfDay.travelId = it
                prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_ID, it).observeOn(Schedulers.io()).blockingAwait()
                travelModel.createTravelOfDay(travelOfDay)
            }
            .subscribe { t ->
                prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_ID, t).observeOn(Schedulers.io()).blockingAwait()
                travelOfDay.travelId = t
                loadTravelingOfDay()
            }
        addDisposable(disposable)
        val timeCheckWork = PeriodicWorkRequestBuilder<TimeCheckWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(WorkerName.TRAVELING_OF_DAY_CHECK.name,ExistingPeriodicWorkPolicy.KEEP,timeCheckWork)
    }

    fun click(){
        isClick.set(!isClick.get())
    }
}
