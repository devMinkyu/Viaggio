package com.kotlin.viaggio.data.source

import androidx.annotation.Keep
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface OpenWeatherApiService {
    @GET("group?")
    fun getCurrentWeather(@Query("localId") ids:String,
                          @Query("units") units:String,
                          @Query("APPID") APPID:String
    ): Single<Response<JsonObject>>
}