package com.kotlin.viaggio.view.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.google.gson.Gson
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.RxEventBus
import com.kotlin.viaggio.worker.DataFetchWorker
import com.kotlin.viaggio.worker.DeleteTravelWorker
import com.kotlin.viaggio.worker.UpdateTravelWorker
import com.kotlin.viaggio.worker.UploadTravelWorker
import dagger.Lazy
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

abstract class BaseViewModel:ViewModel() {
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var rxEventBus: RxEventBus
    @Inject
    lateinit var gson: Gson

    private val disposables = mutableListOf<Disposable?>()

    val constraints:Constraints by lazy {
        if (prefUtilService.getInt(AndroidPrefUtilService.Key.UPLOAD_MODE).blockingGet() == 0) {
            Constraints.Builder()
        } else {
            Constraints.Builder().setRequiresCharging(true)
        }.setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    override fun onCleared() {
        for (disposable in disposables) {
            disposable?.dispose()
        }
        disposables.clear()
    }
    @Synchronized fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }
    open fun initialize() {}

    fun syncDataFetch() {
        val con = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val work = OneTimeWorkRequestBuilder<DataFetchWorker>()
            .setConstraints(con)
            .build()
        WorkManager.getInstance(appCtx.get()).enqueue(work)
    }

    fun dataFetch() {
        val con = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val work = PeriodicWorkRequestBuilder<DataFetchWorker>(3, TimeUnit.DAYS)
            .setConstraints(con)
            .build()
//        val work = OneTimeWorkRequestBuilder<DataFetchWorker>()
//            .setConstraints(con)
//            .build()
        WorkManager.getInstance(appCtx.get()).enqueue(work)
    }

    fun deleteWork(data: Any) {
        val workData = loadData(data)
        val travelWork = OneTimeWorkRequestBuilder<DeleteTravelWorker>()
            .setConstraints(constraints)
            .setInputData(workData)
            .build()
        WorkManager.getInstance(appCtx.get()).enqueue(travelWork)
    }

    fun updateWork(data: Any) {
        val workData = loadData(data)
        val travelWork = OneTimeWorkRequestBuilder<UpdateTravelWorker>()
            .setConstraints(constraints)
            .setInputData(workData)
            .build()
        WorkManager.getInstance(appCtx.get()).enqueue(travelWork)
    }

    fun uploadWork(data: Any) {
        val workData = loadData(data)
        val travelWork = OneTimeWorkRequestBuilder<UploadTravelWorker>()
            .setConstraints(constraints)
            .setInputData(workData)
            .build()
        WorkManager.getInstance(appCtx.get()).enqueue(travelWork)
    }

    private fun loadData(data: Any): Data{
        val resultJsonData = gson.toJson(data)
        return when (data) {
            data as? Travel -> {
                Data.Builder()
                    .putString(WorkerName.TRAVEL.name, resultJsonData)
                    .build()
            }
            data as? TravelCard -> {
                Data.Builder()
                    .putString(WorkerName.TRAVEL_CARD.name, resultJsonData)
                    .build()
            }
            else -> {
                Data.Builder().build()
            }
        }
    }

}