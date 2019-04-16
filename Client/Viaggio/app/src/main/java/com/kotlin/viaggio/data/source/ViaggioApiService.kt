package com.kotlin.viaggio.data.source

import androidx.annotation.Keep
import com.kotlin.viaggio.data.`object`.SignInBody
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.ViaggioApiAuth
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

@Keep
interface ViaggioApiService {
    @POST("api/v1/auth/signup")
    @FormUrlEncoded
    fun signUp(@Field("email") email:String,
               @Field("name") name:String,
               @Field("passwordHash") passwordHash:String,
               @Field("passwordHash2") passwordHash2:String
               ): Single<Response<ViaggioApiAuth>>

    @POST("api/v1/users/login")
    fun signIn(@Body body: SignInBody): Single<Response<ViaggioApiAuth>>

    @POST("/api/v1/users/changepwd")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded",
        "Authorization: ${1}"
    )
    @FormUrlEncoded
    fun updateUserPaswword(@Body body: SignInBody): Single<Response<ViaggioApiAuth>>

    @POST
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    fun uploadTravel(@Header("Authorization") token:String, @Body travel: Travel): Completable
}