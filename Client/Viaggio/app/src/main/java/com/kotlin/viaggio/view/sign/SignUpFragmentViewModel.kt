package com.kotlin.viaggio.view.sign

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.data.`object`.Error
import com.kotlin.viaggio.data.`object`.SignError
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.view.sign.common.Encryption
import com.tag_hive.saathi.saathi.error.InvalidFormException
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import java.net.HttpURLConnection
import javax.inject.Inject

class SignUpFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel
    @Inject
    lateinit var gson:Gson
    val name = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val email = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val password = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val confirmPassword = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val isFormValid = ObservableBoolean(false)
    val errorMsg = ObservableField<String?>()

    private var validateFormDisposable: Disposable? = null
    val error: MutableLiveData<Event<SignError>> = MutableLiveData()
    var complete: MutableLiveData<Event<Any>> = MutableLiveData()

    private fun validateForm() {
        validateFormDisposable?.dispose()
        validateFormDisposable = Maybe
            .create<Any> {
                when {
                    TextUtils.isEmpty(name.get()) -> throw InvalidFormException()
                    TextUtils.isEmpty(email.get()) -> throw InvalidFormException()
                    TextUtils.isEmpty(password.get()) -> throw InvalidFormException()
                    TextUtils.isEmpty(confirmPassword.get()) -> throw InvalidFormException()
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
    fun validateSignUp(): Boolean {
        if (password.get() != confirmPassword.get()) {
            error.value = Event(SignError.PW_MISMATCH)
            return false
        }
        val isValidAlphaNumericUnderscoreId = Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()
        isValidAlphaNumericUnderscoreId.let {
            if (!it) {
                error.value = Event(SignError.INVALID_EMAIL_FORMAT)
                return false
            }
        }

        val encryptionPassword = Encryption().encryptionValue(password.get()!!)
        val encryptionPassword2 = Encryption().encryptionValue(confirmPassword.get()!!)

        val disposable = userModel.signUp(name = name.get()!!, email = email.get()!!, password = encryptionPassword, password2 = encryptionPassword2)
            .subscribe ({ t1->
                if (t1.isSuccessful){
                    complete.postValue(Event(Any()))
                }else{
                    val errorMsg: Error = gson.fromJson(t1.errorBody()?.string(), Error::class.java)
                    when(errorMsg.message){
                        401 -> error.postValue(Event(SignError.EXIST_EMAIL))
                    }
                }
            }){
                Log.d("hoho", "$it")
            }
        addDisposable(disposable)
        return true
    }
}
