package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kotlin.viaggio.ioc.module.common.AndroidWorkerInjection

abstract class BaseWorker(context: Context, params: WorkerParameters):Worker(context, params){
    override fun doWork(): Result {
        AndroidWorkerInjection.inject(this)
        return Result.success()
    }
}