package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kotlin.viaggio.android.TimeHelper
import com.kotlin.viaggio.ioc.module.common.AndroidWorkerInjection
import com.kotlin.viaggio.model.TravelModel
import dagger.Lazy
import javax.inject.Inject
import javax.inject.Named

abstract class BaseWorker(context: Context, params: WorkerParameters):Worker(context, params){
    @Inject
    lateinit var timeHelper: TimeHelper
    @Inject
    lateinit var travelModel: TravelModel
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>

    override fun doWork(): Result {
        AndroidWorkerInjection.inject(this)
        return Result.success()
    }
}