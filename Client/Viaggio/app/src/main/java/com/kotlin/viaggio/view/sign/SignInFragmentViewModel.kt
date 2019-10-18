package com.kotlin.viaggio.view.sign

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.Error
import com.kotlin.viaggio.data.obj.SignError
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.view.sign.common.Encryption
import com.tag_hive.saathi.saathi.error.InvalidFormException
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class SignInFragmentViewModel @Inject constructor():BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel
    @Inject
    lateinit var encryption: Encryption
    lateinit var googleSignInClient: GoogleSignInClient
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

    private var validateFormDisposable: Disposable? = null
    val error: MutableLiveData<Event<SignError>> = MutableLiveData()
    val googleErrorMsg = MutableLiveData<Event<String>>()
    var complete: MutableLiveData<Event<Any>> = MutableLiveData()

    override fun initialize() {
        super.initialize()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(appCtx.get().getString(R.string.server_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(appCtx.get(), gso)
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
        val encryptionPassword = encryption.encryptionValue(password.get()!!)

        val disposable = userModel.signIn(email.get()!!, encryptionPassword)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ t1->
                if(t1.isSuccessful){
                    complete.value = Event(Any())
                }else{
                    val errorMsg: Error = gson.fromJson(t1.errorBody()?.string(), Error::class.java)
                    when(errorMsg.message){
                        401 -> error.postValue(Event(SignError.EMAIL_NOT_FOUND))
                        400 -> error.postValue(Event(SignError.WRONG_PW))
                    }
                }
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }

    fun handleSignInResult(account: GoogleSignInAccount?) {
        account?.idToken?.let { idToken ->
            userModel.googleSignIn(idToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if(it.isSuccessful) {
                        complete.value = Event(Any())
                    } else {
                        val errorMsg: Error = gson.fromJson(it.errorBody()?.string(), Error::class.java)
                        when(errorMsg.message){
                            400 -> googleErrorMsg.value = Event(resources.getString(R.string.err_exist_email))
                        }
                    }
                }) {
                    Timber.d(it)
                }
        }
    }
}
