package com.kotlin.viaggio.view.setting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

class LogoutActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel

    val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()
    fun logout() {
        val completables = mutableListOf<Completable>()

        Log.d("hoho", "${prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()}")
        val disposable = userModel.logOut().andThen {
//            completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.AWS_TOKEN, ""))
//            completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.AWS_ID, ""))
//            completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.TOKEN_ID, ""))
//            completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE, ""))
//            completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.USER_NAME, ""))
//            completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.USER_ID, ""))
//            Completable.merge(completables)
            Completable.complete()
        }.subscribe({
            rxEventBus.userUpdate.onNext(Any())
            completeLiveDate.postValue(Event(Any()))
        }) {
            Timber.d(it)
        }
        addDisposable(disposable)
    }
}