package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.source.AppDatabase
import com.kotlin.viaggio.data.source.LocalDataSource
import dagger.Lazy
import javax.inject.Inject

abstract class BaseModel{
    @Inject
    lateinit var localDataSource: LocalDataSource
    @Inject
    lateinit var db: Lazy<AppDatabase>
}