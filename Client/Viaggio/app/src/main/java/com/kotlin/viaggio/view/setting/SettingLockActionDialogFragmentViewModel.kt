package com.kotlin.viaggio.view.setting

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.reprint.core.AuthenticationResult
import com.github.ajalt.reprint.core.Reprint
import com.github.ajalt.reprint.rxjava2.RxReprint
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class SettingLockActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel

    var enrollMode = ObservableBoolean(false)
    var isExistFingerPrint = ObservableBoolean(false)
    val password = ObservableArrayList<Int>()
    var confirmPassword = ""

    val completeLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val fingerPrintHelpLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    var fingerPrint = false
    var currentPosition = 0

    var fingerPrintDisposable:Disposable? = null
    override fun initialize() {
        super.initialize()
        settingPassword()
        Reprint.initialize(appCtx.get())
        fingerPrint = prefUtilService.getBool(AndroidPrefUtilService.Key.FINGER_PRINT_LOCK_APP).blockingGet()

        if(enrollMode.get().not()){
            fingerPrintCheck()
        }
    }
    fun fingerPrintCheck(){
        fingerPrintDisposable?.dispose()
        if (fingerPrint) {
            isExistFingerPrint.set(fingerPrint)
            val disposable = RxReprint.authenticate()
                .subscribe({
                    when (it.status) {
                        AuthenticationResult.Status.SUCCESS -> {
                            fingerPrintHelpLiveData.value = Event(true)
                        }
                        AuthenticationResult.Status.NONFATAL_FAILURE -> {
                            fingerPrintHelpLiveData.value = Event(false)
                        }
                        AuthenticationResult.Status.FATAL_FAILURE -> {
                            isExistFingerPrint.set(false)
                        }
                        else -> {

                        }
                    }
                }){
                    Timber.d(it)
                }
            fingerPrintDisposable = disposable
        }
    }
    private fun settingPassword(){
        password.clear()
        currentPosition = 0
        for(i in 0 until 4){
            password.add(null)
        }
    }
    fun choose(num:Int){
        password[currentPosition] = num
        currentPosition += 1
        if(currentPosition == 4){
            if(enrollMode.get()){
                if(TextUtils.isEmpty(confirmPassword)){
                    confirmPassword = password.joinToString()
                    settingPassword()
                } else {
                    val pw = password.joinToString()
                    if(pw == confirmPassword){
                        prefUtilService.putString(AndroidPrefUtilService.Key.LOCK_PW, pw).blockingAwait()
                        prefUtilService.putBool(AndroidPrefUtilService.Key.LOCK_APP, true).blockingAwait()
                        rxEventBus.completeLock.onNext(Any())
                        completeLiveData.value = Event(true)
                    } else {
                        settingPassword()
                        completeLiveData.value = Event(false)
                    }

                }
            } else {
                val lockPassword = password.joinToString()
                if(lockPassword == prefUtilService.getString(AndroidPrefUtilService.Key.LOCK_PW).blockingGet()){
                    completeLiveData.value = Event(true)
                } else {
                    settingPassword()
                    completeLiveData.value = Event(false)
                }
            }
        }
    }
    fun cancel() {
        if(currentPosition != 0){
            currentPosition -= 1
            password[currentPosition] = null
        }
    }

    override fun onCleared() {
        super.onCleared()
        fingerPrintDisposable?.dispose()
    }
}