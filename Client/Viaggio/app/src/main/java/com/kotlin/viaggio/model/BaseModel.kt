package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.source.LocalDataSource
import javax.inject.Inject

abstract class BaseModel{
    @Inject
    lateinit var localDataSource: LocalDataSource
}