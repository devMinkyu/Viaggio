package com.kotlin.viaggio.view.setting

import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.TimeCheckWorker
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LogoutActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel

    val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()
    fun logout() {
        val completables = mutableListOf<Completable>()

        completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.AWS_TOKEN, ""))
        completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.AWS_ID, ""))
        completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.TOKEN_ID, ""))
        completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE, ""))
        completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.USER_NAME, ""))
        completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.USER_ID, ""))

        val disposable = Completable.merge(completables)
            .subscribe({
                rxEventBus.userUpdate.onNext(Any())
                completeLiveDate.postValue(Event(Any()))
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }
}