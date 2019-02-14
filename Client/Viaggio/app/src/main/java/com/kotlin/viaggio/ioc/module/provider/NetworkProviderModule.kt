package com.kotlin.viaggio.ioc.module.provider

import com.google.gson.Gson
import com.kotlin.viaggio.data.source.ViaggioApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkProviderModule{
    @Provides
    @Singleton
    internal fun provideRemoteDataSource(
        client: OkHttpClient,
        gson: Gson
    ): ViaggioApiService {
        val gsonConverterFactory = GsonConverterFactory.create(gson)

        val tagClient = client.newBuilder()
            .retryOnConnectionFailure(true)
            .build()

//        val baseUrl = BuildConfig.SERVICE_HOST
        val baseUrl = ""

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
    internal fun provideGson(): Gson {
        return Gson()
    }
    @Provides
    @Singleton
    internal fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

}