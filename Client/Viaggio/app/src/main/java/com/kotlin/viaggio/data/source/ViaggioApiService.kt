package com.kotlin.viaggio.data.source

import android.text.TextUtils
import androidx.annotation.Keep
import com.kotlin.viaggio.data.`object`.Area
import com.kotlin.viaggio.data.`object`.ViaggioApiAWSAuth
import com.kotlin.viaggio.data.`object`.ViaggioApiAuth
import com.kotlin.viaggio.data.`object`.ViaggioResult
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.Interceptor
import retrofit2.Response
import retrofit2.http.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Keep
interface ViaggioApiService {
    // user
    @POST("api/v1/auth/signup")
    @FormUrlEncoded
    fun getAws(): Single<Response<ViaggioApiAWSAuth>>

    @POST("api/v1/auth/signup")
    @Headers("No-Authentication: true")
    @FormUrlEncoded
    fun signUp(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("passwordHash") passwordHash: String,
        @Field("passwordHash2") passwordHash2: String
    ): Single<Response<ViaggioApiAuth>>

    @POST("api/v1/auth/login")
    @Headers("No-Authentication: true")
    @FormUrlEncoded
    fun signIn(
        @Field("email") email: String,
        @Field("passwordHash") passwordHash: String
    ): Single<Response<ViaggioApiAuth>>

    @POST("api/v1/users/changeinfo")
    @FormUrlEncoded
    fun updateUserName(
        @Field("name") name: String,
        @Field("profileImageName") profileImageName: String,
        @Field("profileImageUrl") profileImageUrl: String
    ): Single<Response<ViaggioResult>>

    @POST("api/v1/users/changepwd")
    @FormUrlEncoded
    fun updateUserPassword(
        @Field("oldPasswordHash") oldPasswordHash: String,
        @Field("passwordHash") passwordHash: String,
        @Field("passwordHash2") passwordHash2: String
    ): Single<Response<ViaggioResult>>

    @GET("api/v1/users/logout")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    fun logOut(): Single<Response<ViaggioResult>>

    // travel
    @POST("api/v1/my/travels")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun uploadTravel(
        @Field("localId") id: Long,
        @Field("area") area: MutableList<Area>,
        @Field("title") title: String,
        @Field("travelKind") travelKind: Int,
        @Field("theme") theme: MutableList<String>,
        @Field("startDate") startDate: Date,
        @Field("endDate") endDate: Date?
    ): Single<Response<Any>>

    @PUT("api/v1/my/travels/{travelId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun updateTravel(
        @Path("travelId") travelId: Long,
        @Header("Authorization") token: String
//        @Field("localId") id: Long,
//        @Field("area") area: MutableList<Area>,
//        @Field("title") title: String,
//        @Field("travelKind") travelKind: Int,
//        @Field("theme") theme: MutableList<String>,
//        @Field("startDate") startDate: Date,
//        @Field("endDate") endDate: Date?
    ): Single<Response<Any>>

    @DELETE("api/v1/my/travels/{travelId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun deleteTravel(
        @Path("travelId") travelId: Long,
        @Header("Authorization") token: String,
        @Field("isDelete") isDelete: Boolean
    ): Single<Response<Any>>

    // travelCard
    @POST("api/v1/my/travelcards/{travelId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun uploadTravelCard(
        @Path("travelId") travelId: Long,
        @Header("Authorization") token: String,
        @Field("travelOfDay") travelOfDay: Int,
        @Field("country") country: String,
        @Field("content") content: String
    ): Single<Response<Any>>

    @PUT("api/v1/my/travelcards/{travelCardId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun updateTravelCard(
        @Path("travelCardId") travelCardId: Long,
        @Header("Authorization") token: String
    ): Single<Response<Any>>

    @DELETE("api/v1/my/travelcards/{travelCardId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun deleteTravelCard(
        @Path("travelCardId") travelCardId: Long,
        @Header("Authorization") token: String,
        @Field("isDelete") isDelete: Boolean
    ):Single<Response<Any>>


    @Singleton
    class TokenInterceptor @Inject constructor(@Named("ApiToken") apiToken: String) : Interceptor {
        private val mApiToken = BehaviorSubject.create<String>()

        init {
            if (!TextUtils.isEmpty(apiToken)) {
                mApiToken.onNext(apiToken)
            }
        }

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            return if (request.header("No-Authentication") == null) {
                val newRequest = request.newBuilder()
                    .addHeader("authorization", mApiToken.blockingFirst())
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(request)
            }
        }
    }
}