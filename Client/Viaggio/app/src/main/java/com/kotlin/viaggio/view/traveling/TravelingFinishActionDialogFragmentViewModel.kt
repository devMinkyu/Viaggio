package com.kotlin.viaggio.view.traveling

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.obj.Travel
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
        var travel = Travel()
        val disposable = travelLocalModel.getTravel()
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                travel = it
                val completables = mutableListOf<Completable>()
                completables.add(prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, false))
                completables.add(prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_KINDS, -1))
                completables.add(prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 0))
                completables.add(prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, ""))
                completables.add(prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_ID, 0))

                travel.endDate = Date(System.currentTimeMillis())
                travel.userExist = false
                completables.add(travelLocalModel.updateTravel(travel))

                Completable.merge(completables)
            }.andThen {
                val token = travelLocalModel.getToken()
                val mode = travelLocalModel.getUploadMode()
                if (TextUtils.isEmpty(token).not() && mode != 2 && travel.serverId != 0) {
                    updateWork(travel)
                    it.onComplete()
                } else {
                    it.onComplete()
                }
            }
            .subscribe({
                val timeCheckWork = PeriodicWorkRequestBuilder<TimeCheckWorker>(1, TimeUnit.DAYS).build()
                WorkManager.getInstance(appCtx.get()).enqueueUniquePeriodicWork(
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