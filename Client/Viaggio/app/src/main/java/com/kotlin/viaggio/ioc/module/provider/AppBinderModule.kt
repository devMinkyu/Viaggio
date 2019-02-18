package com.kotlin.viaggio.ioc.module.provider

import androidx.work.Worker
import com.kotlin.viaggio.ioc.component.DataFetchWorkerSubComponent
import com.kotlin.viaggio.ioc.module.mapkey.WorkerKey
import com.kotlin.viaggio.worker.BaseWorker
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds

@Suppress("unused")
@Module(
    includes = [
        DataFetchWorkerSubComponent::class
    ]
)
abstract class AppBinderModule {
    @Multibinds
    abstract fun supportWorkerInjectorFactories(): Map<Class<out Worker>, AndroidInjector.Factory<out Worker>>

    @Multibinds
    abstract fun supportWorkerInjectorFactoriesWithStringKeys(): Map<String, AndroidInjector.Factory<out Worker>>


    @Binds
    @IntoMap
    @WorkerKey(BaseWorker::class)
    abstract fun bindMyWorkerFactory(builder: DataFetchWorkerSubComponent.Builder): AndroidInjector.Factory<out Worker>
}