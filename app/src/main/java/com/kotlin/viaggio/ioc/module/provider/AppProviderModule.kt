@file:Suppress("unused")

package com.kotlin.viaggio.ioc.module.provider

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.kotlin.viaggio.view.App
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes =
    [
        NetworkProviderModule::class
    ]
)
class AppProviderModule {
    @Provides
    @Singleton
    @Named("Application")
    internal fun provideApplicationContext(app: App): Context {
        return app.baseContext
    }

    @Provides
    internal fun providePreferenceManager(@Named("Application") context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    internal fun provideRes(@Named("Application") context: Context): Resources {
        return context.resources
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return Gson()
    }
}