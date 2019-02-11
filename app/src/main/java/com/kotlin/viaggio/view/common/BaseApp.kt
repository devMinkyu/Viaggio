package com.kotlin.viaggio.view.common

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import androidx.multidex.MultiDexApplication
import dagger.android.*
import javax.inject.Inject

abstract class BaseApp : MultiDexApplication(), HasActivityInjector, HasServiceInjector,
    HasBroadcastReceiverInjector, HasContentProviderInjector {
    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>
    @Inject
    lateinit var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject
    lateinit var contentProviderInjector: DispatchingAndroidInjector<ContentProvider>

    private var needToInject: Boolean = true

    protected abstract fun applicationInjector(): AndroidInjector<out BaseApp>
    override fun onCreate() {
        super.onCreate()

        if (needToInject) {
            synchronized(this) {
                if (needToInject) {
                    @Suppress("UNCHECKED_CAST")
                    val applicationInjector = applicationInjector() as AndroidInjector<BaseApp>
                    applicationInjector.inject(this)
                    if (needToInject) {
                        throw IllegalStateException(
                            "The AndroidInjector returned from applicationInjector() did not inject the " + "DaggerApplication"
                        )
                    }
                }
            }
        }
    }

    @Inject
    internal fun setInjected() {
        needToInject = false
    }

    override fun activityInjector() = activityInjector
    override fun serviceInjector() = serviceInjector
    override fun broadcastReceiverInjector() = broadcastReceiverInjector
    override fun contentProviderInjector() = contentProviderInjector
}