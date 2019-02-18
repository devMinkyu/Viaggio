package com.kotlin.viaggio.ioc.module.mapkey

import androidx.work.Worker
import dagger.MapKey
import dagger.internal.Beta
import kotlin.reflect.KClass

/** [MapKey] annotation to key bindings by a type of an [androidx.work.Worker].  */
@Beta
@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.PROPERTY_GETTER)
annotation class WorkerKey(val value: KClass<out Worker>)