package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.`object`.SignInBody
import com.kotlin.viaggio.data.`object`.SignUpBody
import com.kotlin.viaggio.data.`object`.ViaggioApiAuth
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserModel @Inject constructor() {
    @Inject
    lateinit var api: ViaggioApiService
    @Inject
    lateinit var pref: AndroidPrefUtilService

    fun signIn(email:String, password:String):Single<Response<ViaggioApiAuth>> =
            api.signIn(SignInBody(email, password))
                .doOnSuccess {
                    it.body()?.also {auth ->
                        pref.putString(AndroidPrefUtilService.Key.TOKEN_ID, auth.viaggioCustomToken).subscribe()
                        pref.putString(AndroidPrefUtilService.Key.USER_ID, auth.docId).subscribe()
                    }
                }
                .subscribeOn(Schedulers.io())

    fun signUp(name:String, email: String, password: String):Single<Response<ViaggioApiAuth>> =
            api.signUp(SignUpBody(name, email = email, password = password))
                .doOnSuccess {
                    it.body()?.also {auth ->
                        pref.putString(AndroidPrefUtilService.Key.TOKEN_ID, auth.viaggioCustomToken).subscribe()
                        pref.putString(AndroidPrefUtilService.Key.USER_ID, auth.docId).subscribe()
                    }
                }
                .subscribeOn(Schedulers.io())
}