package com.kotlin.viaggio.data.source

import androidx.annotation.Keep
import com.kotlin.viaggio.data.obj.*
import io.reactivex.Single
import okhttp3.Interceptor
import retrofit2.Response
import retrofit2.http.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Keep
interface ViaggioApiService {
    // user
    @GET("api/v1/my/aws")
    @Headers("No-Authentication: true")
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
    fun logOut(): Single<Response<ViaggioResult>>


    // travel
    @POST("api/v1/my/travels")
    @FormUrlEncoded
    fun uploadTravel(
        @Field("localId") localId: Long,
        @Field("area") area: MutableList<Area>,
        @Field("title") title: String,
        @Field("travelKind") travelKind: Int,
        @Field("theme") theme: MutableList<String>,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String?,
        @Field("imageName") imageName: String,
        @Field("imageUrl") imageUrl: String,
        @Field("share") share: Boolean,
        @Field("isDelete") isDelete: Boolean
    ): Single<Response<ViaggioTravelResult>>

    @PUT("api/v1/my/travels/{serverId}")
    @FormUrlEncoded
    fun updateTravel(
        @Path("serverId") serverId: Int,
        @Field("area") area: MutableList<Area>,
        @Field("title") title: String,
        @Field("theme") theme: MutableList<String>,
        @Field("endDate") endDate: Date?,
        @Field("imageName") imageName: String,
        @Field("imageUrl") imageUrl: String,
        @Field("share") share: Boolean
    ): Single<Response<Any>>

    @DELETE("api/v1/my/travels/{serverId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun deleteTravel(
        @Path("serverId") serverId: Int
    ): Single<Response<Any>>

    // travelCard
    @POST("api/v1/my/travelcards/{travelServerId}")
    @FormUrlEncoded
    fun uploadTravelCard(
        @Path("travelServerId") travelServerId: Int,
        @Field("localId") localId: Long,
        @Field("travelLocalId") travelLocalId: Long,
        @Field("travelOfDay") travelOfDay: Int,
        @Field("theme") theme: MutableList<String>,
        @Field("imageName") imageName: MutableList<String>,
        @Field("imageUrl") imageUrl: MutableList<String>,
        @Field("date") date: String,
        @Field("isDelete") isDelete: Boolean,
        @Field("country") country: String,
        @Field("content") content: String
    ): Single<Response<ViaggioTravelResult>>

    @PUT("api/v1/my/travelcards/{serverId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun updateTravelCard(
        @Path("serverId") serverId: Int,
        @Field("content") content: String
    ): Single<Response<Any>>

    @DELETE("api/v1/my/travelcards/{serverId}")
    @Headers(
        "Content-Type: application/x-www-form-urlencoded"
    )
    @FormUrlEncoded
    fun deleteTravelCard(
        @Path("serverId") serverId: Int
    ):Single<Response<Any>>


    @Singleton
    class TokenInterceptor @Inject constructor() : Interceptor {
        @Inject
        lateinit var prefUtilService: AndroidPrefUtilService

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            val token = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()
            return if (request.header("No-Authentication") == null) {
                val newRequest = request.newBuilder()
                    .addHeader("auth", token)
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(request)
            }
        }
    }
}