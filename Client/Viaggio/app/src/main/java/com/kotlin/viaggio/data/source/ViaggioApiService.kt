package com.kotlin.viaggio.data.source

import androidx.annotation.Keep
import com.kotlin.viaggio.data.`object`.SignInBody
import com.kotlin.viaggio.data.`object`.SignUpBody
import com.kotlin.viaggio.data.`object`.ViaggioApiAuth
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

@Keep
interface ViaggioApiService {
    @POST("signUp")
    fun signUp(@Body body: SignUpBody): Single<Response<ViaggioApiAuth>>

    @POST("signIn")
    fun signIn(@Body body: SignInBody): Single<Response<ViaggioApiAuth>>
}