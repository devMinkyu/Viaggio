package com.kotlin.viaggio.view.sign

import android.text.TextUtils
import android.util.Log
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

class SignInFragmentViewModel @Inject constructor():BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel

    val email = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object :androidx.databinding.Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: androidx.databinding.Observable?, propertyId: Int) {
                validateForm()
            }
        })
    }
    val password = ObservableField<String>().apply {
        addOnPropertyChangedCallback(object :androidx.databinding.Observable.OnPropertyChangedCallback(){
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
                    TextUtils.isEmpty(email.get()) -> throw InvalidFormException()
                    TextUtils.isEmpty(password.get()) -> throw InvalidFormException()
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
    fun validateSignIn() {
        val encryptionPassword = Encryption().encryptionValue(password.get()!!)
        error.value = null
        val disposable = userModel.signIn(email.get()!!, encryptionPassword)
            .subscribe { t1, t2 ->
                if(t1.isSuccessful){
                    complete.value = Any()
                }else{
                    when(t1.code()){
                        HttpURLConnection.HTTP_NOT_FOUND -> error.value = SignError.EMAIL_NOT_FOUND
                    }
                }
            }
        addDisposable(disposable)
    }
}
