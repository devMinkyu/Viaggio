package com.kotlin.viaggio.view.setting

import android.text.TextUtils
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.Error
import com.kotlin.viaggio.data.obj.PermissionError
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class SettingMyProfileFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel
    @Inject
    lateinit var transferUtility: TransferUtility
    @Inject
    lateinit var config: DeveloperAuthenticationProvider

    val email = ObservableField("")
    val name = ObservableField("").apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                check()
            }
        })
    }
    val isFormValid = ObservableBoolean(false)

    val completeLiveData = MutableLiveData<Event<Any>>()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    val imageViewShow: MutableLiveData<Event<Any>> = MutableLiveData()

    var imageName = ""
    val imageNameLiveData = MutableLiveData<Event<String>>()
    override fun initialize() {
        super.initialize()
        email.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet())
        name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
        imageName = prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE).blockingGet()
        imageNameLiveData.value = Event(imageName)

        val imageDisposable = rxEventBus.userImage
            .subscribe {
                imageName = it
                imageNameLiveData.value = Event(imageName)
            }
        addDisposable(imageDisposable)
    }
    fun check() {
        when {
            TextUtils.isEmpty(name.get()) -> isFormValid.set(false)
            else -> isFormValid.set(true)
        }
    }

    fun permissionCheck(request: io.reactivex.Observable<Boolean>?) {
        val disposable = request?.subscribe { t ->
            if (t) {
                imageViewShow.value = Event(Any())
            } else {
                permissionRequestMsg.value = Event(PermissionError.STORAGE_PERMISSION)
            }
        }
        disposable?.let { addDisposable(it) }
    }

    fun save() {
        val newAws = prefUtilService.getBool(AndroidPrefUtilService.Key.NEW_AWS).blockingGet()
        val image = prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE).blockingGet()
        val disposable = if(imageName == image){
            userModel.updateUser(name.get()!!, prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE_URL).blockingGet())
        }else{
            userModel.userProfile(imageName)
                .flatMap {imageName ->
                    if (TextUtils.isEmpty(image).not()) {
                        File(image).delete()
                    }
                    if(newAws) {
                        imageAwsSave(imageName)
                    } else {
                        userModel.getAws()
                            .flatMap {
                                imageAwsSave(imageName)
                            }
                    }
                    .subscribeOn(Schedulers.io())
                }
                .flatMap {
                    prefUtilService.putString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE_URL, it).blockingAwait()
                    userModel.updateUser(name.get()!!, it)
                }
        }
            .observeOn(Schedulers.io())
            .subscribe({
                if (it.isSuccessful) {
                    prefUtilService.putString(AndroidPrefUtilService.Key.USER_NAME, name.get()!!).blockingAwait()
                    rxEventBus.userUpdate.onNext(Any())
                    completeLiveData.postValue(Event(Any()))
                } else {
                    val errorMsg: Error = gson.fromJson(it.errorBody()?.string(), Error::class.java)
                    when (errorMsg.message) {
                        401 -> {
                            // 토큰 만료
                        }
                    }
                }
            }) {
                Timber.d(it)
            }

        addDisposable(disposable)
    }

    private fun imageAwsSave(imageName:List<String>) =
        Single.create<String> { emitter ->
            prefUtilService.putString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE, imageName.first()).blockingAwait()

            val awsId = prefUtilService.getString(AndroidPrefUtilService.Key.AWS_ID).blockingGet()
            val awsToken = prefUtilService.getString(AndroidPrefUtilService.Key.AWS_TOKEN).blockingGet()
            config.setInfo(awsId, awsToken)
            val uploadObserver = transferUtility.upload(BuildConfig.S3_UPLOAD_BUCKET, "users/${imageName.first().split("/").last()}", File(imageName.first()))
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if(state == TransferState.COMPLETED){
                        emitter.onSuccess(uploadObserver.key)
                    }
                }
                override fun onError(id: Int, ex: Exception?) {
                    emitter.onError(ex as Throwable)
                }
            })
        }
}
