package com.kotlin.viaggio.view.traveling

import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.kotlin.viaggio.worker.TimeCheckWorker
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TravelingFinishActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()
    fun travelingFinish() {
        val disposable = travelLocalModel.getTravel()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, false).blockingAwait()

                prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 0).blockingAwait()
                prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, 0).blockingAwait()

                prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, "").blockingAwait()

                prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_ID, 0).blockingAwait()

                it.endDate = Date(System.currentTimeMillis())
                travelLocalModel.updateTravel(it)
            }
            .subscribe({
                val timeCheckWork = PeriodicWorkRequestBuilder<TimeCheckWorker>(1, TimeUnit.DAYS).build()
                WorkManager.getInstance().enqueueUniquePeriodicWork(
                    WorkerName.TRAVELING_OF_DAY_CHECK.name,
                    ExistingPeriodicWorkPolicy.REPLACE, timeCheckWork
                )
                rxEventBus.travelFinish.onNext(true)
                completeLiveDate.postValue(Event(Any()))
            }){
                Timber.e(it)
            }
        addDisposable(disposable)
    }
}