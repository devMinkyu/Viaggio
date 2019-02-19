package com.kotlin.viaggio.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CompressWorker(context: Context, params:WorkerParameters):BaseWorker(context, params){
    override fun doWork(): Result {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}