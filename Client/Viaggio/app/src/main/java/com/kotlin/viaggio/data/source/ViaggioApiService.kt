package com.kotlin.viaggio.data.source

import androidx.annotation.Keep
import com.kotlin.viaggio.data.`object`.Area
import com.kotlin.viaggio.data.`object`.ViaggioApiAuth
import com.kotlin.viaggio.data.`object`.ViaggioResult
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*

@Keep
interface ViaggioApiService {
    // user
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

    @POST("api/v1/users/changeinfo")
    @FormUrlEncoded
    fun updateUserName(
        @Field("name") name:String,
        @Field("profileImageName") profileImageName:String,
        @Field("profileImageUrl") profileImageUrl:String
    ): Single<Response<ViaggioResult>>

    @POST("api/v1/users/changepwd")
    @FormUrlEncoded
    fun updateUserPaswword(
        @Field("oldPasswordHash") oldPasswordHash:String,
        @Field("passwordHash") passwordHash:String,
        @Field("passwordHash2") passwordHash2:String
    ): Single<Response<ViaggioResult>>





    @POST("api/v1/my/travels")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun uploadTravel(
        @Header("Authorization") token:String,
        @Field("localId") id:Long,
        @Field("area") area: MutableList<Area>,
        @Field("title") title:String,
        @Field("travelKind") travelKind:Int,
        @Field("theme") theme:MutableList<String>,
        @Field("startDate") startDate:Date,
        @Field("endDate") endDate:Date?
    ): Single<Response<Any>>
}


//@Headers(
//    "Content-Type: application/x-www-form-urlencoded",
//    "Authorization: ${1}"
//)