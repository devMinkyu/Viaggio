package com.kotlin.viaggio.ioc.module.common

import android.app.Activity
import androidx.work.Worker
import dagger.android.AndroidInjector

object AndroidWorkerInjection {
    fun inject(worker: Worker) {
        checkNotNull(worker) { "worker" }
        val context = worker.applicationContext
        if (context is HasWorkerInjector) {
            val workerInjector = context.workerInjector()
            checkNotNull(workerInjector) { "${context.javaClass}.workerInjector() return null" }
            try {
                workerInjector.inject(worker)
            } catch (e: Exception) {
                throw RuntimeException("${context.javaClass.canonicalName} does not implement ${HasWorkerInjector::class.java.canonicalName}")
            }

        }
    }

    interface HasWorkerInjector {
        /** Returns an [AndroidInjector] of [Activity]s.  */
        fun workerInjector(): AndroidInjector<Worker>
    }
}