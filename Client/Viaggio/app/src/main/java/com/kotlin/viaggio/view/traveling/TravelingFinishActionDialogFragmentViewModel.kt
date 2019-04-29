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
import io.reactivex.Completable
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
                val completables = mutableListOf<Completable>()
                completables.add(prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, false))
                completables.add(prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 0))
                completables.add(prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, 0))
                completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, ""))
                completables.add(prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_ID, 0))

                it.endDate = Date(System.currentTimeMillis())
                it.userExist = false
                completables.add(travelLocalModel.updateTravel(it))

                Completable.merge(completables)
            }
            .subscribe({
                val timeCheckWork = PeriodicWorkRequestBuilder<TimeCheckWorker>(1, TimeUnit.DAYS).build()
                WorkManager.getInstance().enqueueUniquePeriodicWork(
                    WorkerName.TRAVELING_OF_DAY_CHECK.name,
                    ExistingPeriodicWorkPolicy.REPLACE, timeCheckWork
                )
                rxEventBus.travelUpdate.onNext(Any())
                completeLiveDate.postValue(Event(Any()))
            }){
                Timber.e(it)
            }
        addDisposable(disposable)
    }
}