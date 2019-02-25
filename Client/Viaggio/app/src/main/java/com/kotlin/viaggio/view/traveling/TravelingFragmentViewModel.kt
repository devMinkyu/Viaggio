package com.kotlin.viaggio.view.traveling

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.tag_hive.saathi.saathi.error.InvalidFormException
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
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

    var travelThemeList:List<String> = listOf()

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
        traveling.set(prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING, false).blockingGet())
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
        val travel = Travel(countries = arrayListOf(travelingStartOfCountry.get()!!),
            startOfDay = SimpleDateFormat(appCtx.get().resources.getString(R.string.dateFormat)).parse(travelingStartOfDay.get()!!)
            ,userId = prefUtilService.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet(), theme = travelThemeList.toMutableList() as ArrayList<String>
        )
        Log.d("hoho", "$travel")
        val disposable = travelModel.createTravel(travel).subscribe { t->
            Log.d("hoho", "$t")
        }
        addDisposable(disposable)
    }
}
