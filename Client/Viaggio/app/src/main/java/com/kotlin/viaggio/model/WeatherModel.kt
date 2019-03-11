package com.kotlin.viaggio.model

import com.google.gson.JsonObject
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.data.source.OpenWeatherApiService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherModel @Inject constructor() :BaseModel(){
    @Inject
    lateinit var api:OpenWeatherApiService

    fun currentWeather():Single<Response<JsonObject>>{
        return api.getCurrentWeather(APPID = BuildConfig.OPEN_WEATHER_API_KEY, units = "metric", ids = "DAS")
            .subscribeOn(Schedulers.io())
    }
}