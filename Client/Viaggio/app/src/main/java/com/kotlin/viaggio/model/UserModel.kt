package com.kotlin.viaggio.model

import android.util.Log
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.GoogleSignInBody
import com.kotlin.viaggio.data.obj.ViaggioApiAWSAuth
import com.kotlin.viaggio.data.obj.ViaggioApiAuth
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserModel @Inject constructor() :BaseModel(){
    @Inject
    lateinit var api: ViaggioApiService
    @Inject
    lateinit var config: DeveloperAuthenticationProvider
    @Inject
    lateinit var pref: AndroidPrefUtilService

    fun googleSignIn(idToken:String) : Single<Response<ViaggioApiAuth>> {
        return api.googleSignIn(GoogleSignInBody(idToken))
            .doOnSuccess {
                it.body()?.also { auth ->
                    saveUserInformation(auth)
                    config.setInfo(auth.AWS_IdentityId, auth.AWS_Token)
                }
            }
            .subscribeOn(Schedulers.io())
    }

    fun signIn(email:String, password:String):Single<Response<ViaggioApiAuth>> =
            api.signIn(email = email, passwordHash = password)
                .doOnSuccess {
                    it.body()?.also {auth ->
                        saveUserInformation(auth)
                        config.setInfo(auth.AWS_IdentityId, auth.AWS_Token)
                    }
                }
                .subscribeOn(Schedulers.io())

    fun signUp(name:String, email: String, password: String, password2: String):Single<Response<ViaggioApiAuth>> {
        return api.signUp(name = name, email = email, passwordHash2 = password2, passwordHash = password)
            .doOnSuccess {
                it.body()?.also {auth ->
                    saveUserInformation(auth)
                    config.setInfo(auth.AWS_IdentityId, auth.AWS_Token)
                }
            }
            .subscribeOn(Schedulers.io())
    }
    fun getAws(): Single<Response<ViaggioApiAWSAuth>>{
        return api.getAws()
            .doOnSuccess {
                it.body()?.also {auth ->
                    Completable.mergeArray(
                        pref.putString(AndroidPrefUtilService.Key.AWS_ID, auth.AWS_IdentityId),
                        pref.putString(AndroidPrefUtilService.Key.AWS_TOKEN, auth.AWS_Token),
                        pref.putBool(AndroidPrefUtilService.Key.NEW_AWS, true)
                    )
                    .blockingAwait()
                    config.setInfo(auth.AWS_IdentityId, auth.AWS_Token)
                }
            }
            .subscribeOn(Schedulers.io())
    }

    fun userProfile(imageName:String, profile:String):Single<List<String>>{
        return localDataSource.recordImage(arrayOf(imageName), profile)
    }

    fun updateUser(name:String, profileImageUrl:String): Single<Response<Any>>{
        return api.updateUserName(
            name = name, profileImageUrl = profileImageUrl
        ).subscribeOn(Schedulers.io())
    }

    fun updatePassword(oldPassword: String, password: String, password2: String): Single<Response<Any>>{
        return api.updateUserPassword(oldPasswordHash = oldPassword, passwordHash = password, passwordHash2 = password2)
            .subscribeOn(Schedulers.io())
    }

    fun logOut(): Single<Response<Any>> = api.logOut()
        .subscribeOn(Schedulers.io())

    private fun saveUserInformation(auth:ViaggioApiAuth) {
        Completable.mergeArray(
            pref.putString(AndroidPrefUtilService.Key.USER_ID, auth.email),
            pref.putString(AndroidPrefUtilService.Key.TOKEN_ID, auth.token),
            pref.putString(AndroidPrefUtilService.Key.USER_NAME, auth.name),
            pref.putBool(AndroidPrefUtilService.Key.GOOGLE_LOGIN, auth.isGoogleId),
            pref.putString(AndroidPrefUtilService.Key.AWS_ID, auth.AWS_IdentityId),
            pref.putString(AndroidPrefUtilService.Key.AWS_TOKEN, auth.AWS_Token),
            pref.putBool(AndroidPrefUtilService.Key.NEW_AWS, true)
        ).blockingAwait()
    }
}