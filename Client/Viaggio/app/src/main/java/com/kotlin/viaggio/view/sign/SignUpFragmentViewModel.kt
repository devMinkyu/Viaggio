package com.kotlin.viaggio.view.sign

import android.text.TextUtils
import android.util.Patterns
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.SignError
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
    val error: MutableLiveData<SignError> = MutableLiveData()
    var complete: MutableLiveData<Any> = MutableLiveData()
    override fun initialize() {
        super.initialize()
    }

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
            error.value = SignError.PW_MISMATCH
            return false
        }
        val isValidAlphaNumericUnderscoreId = Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()
        isValidAlphaNumericUnderscoreId.let {
            if (!it) {
                error.value = SignError.INVALID_EMAIL_FORMAT
                return false
            }
        }
        error.value = null
        val encryptionPassword = Encryption().encryptionValue(password.get()!!)

        val disposable = userModel.signUp(name = name.get()!!, email = email.get()!!, password = encryptionPassword)
            .subscribe { t1, t2 ->
                if (t1.isSuccessful){
                    complete.value = Any()
                }else{
                    when(t1.code()){
                        HttpURLConnection.HTTP_CONFLICT -> error.value = SignError.EXIST_EMAIL
                    }
                }
            }
        addDisposable(disposable)
        return true
    }
}
