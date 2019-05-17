package com.kotlin.viaggio.view.setting

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SettingFragmentViewModel @Inject constructor() : BaseViewModel(){
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var userModel: UserModel

    val name = ObservableField<String>("")
    val email = ObservableField<String>("")
    val isLogin = ObservableBoolean(false)
    val appVersion = ObservableField<String>("")
    val lockNotice = ObservableBoolean(false)

    val checkLiveData:MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showDialogLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
    override fun initialize() {
        super.initialize()
        appVersion.set(BuildConfig.VERSION_NAME)
        if(TextUtils.isEmpty(prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()).not()){
            isLogin.set(true)
            email.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet())
            name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
        }

        val disposable = rxEventBus.userUpdate
            .subscribe {
                if(TextUtils.isEmpty(prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet())){
                    isLogin.set(false)
                }else{
                    name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
                }
            }
        addDisposable(disposable)

        val uploadCheckDisposable = rxEventBus.uploadCheck
            .subscribe {value ->
                if(value){
                    showDialogLiveData.value = Event(Any())
                    check()
                }
            }
        addDisposable(uploadCheckDisposable)
        lockNotice.set(prefUtilService.getBool(AndroidPrefUtilService.Key.LOCK_APP).blockingGet())
    }

    fun check(){
        val travelSingle = travelLocalModel.getNotUploadTravels()
        val travelCardSingle = travelLocalModel.getNotUploadTravelCards()

        val disposable = Single.zip(travelSingle, travelCardSingle, BiFunction
        <List<Travel>, List<TravelCard>, Boolean>
        { t1, t2 ->
            !(t1.isNotEmpty() || t2.isNotEmpty())
        }).subscribeOn(Schedulers.io())
            .subscribe({
            checkLiveData.postValue(Event(it))
        }){
            Timber.d(it)
        }
        addDisposable(disposable)
    }
}
