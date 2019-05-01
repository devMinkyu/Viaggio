package com.kotlin.viaggio.view.setting

import android.text.TextUtils
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.data.`object`.Error
import com.kotlin.viaggio.data.`object`.SignError
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.view.sign.common.Encryption
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SettingMyProfileFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel
    @Inject
    lateinit var gson: Gson
    val email = ObservableField<String>("")
    val name = ObservableField<String>("").apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                check()
            }
        })
    }
    val isFormValid = ObservableBoolean(false)

    val completeLiveData = MutableLiveData<Event<Any>>()
    val error: MutableLiveData<Event<SignError>> = MutableLiveData()

    var imageName = ""
    override fun initialize() {
        super.initialize()
        email.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet())
        name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
        imageName = prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE).blockingGet()
    }
    fun check() {
        when {
            TextUtils.isEmpty(name.get()) -> isFormValid.set(false)
            else -> isFormValid.set(true)
        }
    }

    fun save() {
        val disposable = userModel.updateUser(name.get()!!, imageName)
            .subscribe({
                if(it.isSuccessful){
                    prefUtilService.putString(AndroidPrefUtilService.Key.USER_NAME, name.get()!!).blockingAwait()
                    completeLiveData.postValue(Event(Any()))
                }
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }
}
