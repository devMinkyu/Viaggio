package com.kotlin.viaggio.data.source

import androidx.annotation.Keep
import com.kotlin.viaggio.data.`object`.ViaggioApiAuth
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*

@Keep
interface ViaggioApiService {
    @POST("api/v1/auth/signup")
    @FormUrlEncoded
    fun signUp(@Field("email") email:String,
               @Field("name") name:String,
               @Field("passwordHash") passwordHash:String,
               @Field("passwordHash2") passwordHash2:String
               ): Single<Response<ViaggioApiAuth>>

    @POST("api/v1/auth/login")
    @FormUrlEncoded
    fun signIn(
        @Field("email") email:String,
        @Field("passwordHash") passwordHash:String
    ): Single<Response<ViaggioApiAuth>>

//    @POST("/api/v1/users/changepwd")
//    @Headers(
//        "Content-Type: application/x-www-form-urlencoded",
//        "Authorization: ${1}"
//    )
//    @FormUrlEncoded
//    fun updateUserPaswword(@Body body: SignInBody): Single<Response<ViaggioApiAuth>>

    @POST("api/v1/my/travels")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun uploadTravel(
        @Header("Authorization") token:String,
        @Field("localId") id:Long,
        @Field("entireCountry") entireCountries: ArrayList<String>,
        @Field("city") city: ArrayList<String>,
        @Field("title") title:String,
        @Field("travelKind") travelKind:Int,
        @Field("theme") theme:ArrayList<String>,
        @Field("startDate") startDate:Date,
        @Field("endDate") endDate:Date?
    ): Single<Response<Any>>
}