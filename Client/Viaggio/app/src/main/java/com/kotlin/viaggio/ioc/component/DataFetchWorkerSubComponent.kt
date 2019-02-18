package com.kotlin.viaggio.ioc.component

import com.kotlin.viaggio.worker.BaseWorker
import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface DataFetchWorkerSubComponent : AndroidInjector<BaseWorker> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<BaseWorker>()
}