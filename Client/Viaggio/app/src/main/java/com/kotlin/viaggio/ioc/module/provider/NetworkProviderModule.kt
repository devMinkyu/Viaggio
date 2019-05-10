package com.kotlin.viaggio.ioc.module.provider

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.data.source.OpenWeatherApiService
import com.kotlin.viaggio.data.source.ViaggioApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Suppress("unused")
@Module
class NetworkProviderModule{
    @Provides
    @Singleton
    internal fun provideRemoteDataSource(
        client: OkHttpClient,
        tokenInterceptor: ViaggioApiService.TokenInterceptor,
        gson: Gson
    ): ViaggioApiService {
        val gsonConverterFactory = GsonConverterFactory.create(gson)

        val tagClient = client.newBuilder()
            .retryOnConnectionFailure(true)
            .addNetworkInterceptor(StethoInterceptor())
            .addInterceptor(tokenInterceptor)
            .build()

        val baseUrl = BuildConfig.SERVER_HOST

        val retrofit = Retrofit.Builder()
            .client(tagClient)
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(ViaggioApiService::class.java)

    }
    @Provides
    @Singleton
    internal fun provideOpenWeather(
        client: OkHttpClient,
        gson: Gson
    ): OpenWeatherApiService {
        val gsonConverterFactory = GsonConverterFactory.create(gson)

        val tagClient = client.newBuilder()
            .retryOnConnectionFailure(true)
            .build()

        val baseUrl = BuildConfig.OPEN_WEATHER

        val retrofit = Retrofit.Builder()
            .client(tagClient)
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(OpenWeatherApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return Gson()
    }
    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

}