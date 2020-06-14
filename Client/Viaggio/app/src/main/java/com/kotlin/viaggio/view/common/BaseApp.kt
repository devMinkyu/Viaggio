package com.kotlin.viaggio.view.common

import androidx.multidex.MultiDexApplication
import androidx.work.ListenableWorker
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kotlin.viaggio.ioc.module.common.AndroidWorkerInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class BaseApp : MultiDexApplication(), HasAndroidInjector, AndroidWorkerInjection.HasWorkerInjector {
    @Inject
    lateinit var workerInjector: DispatchingAndroidInjector<ListenableWorker>

    @Inject
    lateinit var daggerAndroidInjector: DispatchingAndroidInjector<Any>

    private var needToInject: Boolean = true

    protected abstract fun applicationInjector(): AndroidInjector<out BaseApp>
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        injectIfNecessary()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true


    }

    private fun injectIfNecessary() {
        if (needToInject) {
            synchronized(this) {
                if (needToInject) {
                    @Suppress("UNCHECKED_CAST")
                    val applicationInjector = applicationInjector() as AndroidInjector<BaseApp>
                    applicationInjector.inject(this)
                    check(!needToInject) { "The AndroidInjector returned from applicationInjector() did not inject the " + "DaggerApplication" }
                }
            }
        }
    }

    @Inject
    internal fun setInjected() {
        needToInject = false
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return daggerAndroidInjector
    }
    override fun workerInjector(): AndroidInjector<ListenableWorker> {
        return workerInjector
    }
}
