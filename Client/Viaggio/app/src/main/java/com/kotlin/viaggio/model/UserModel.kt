package com.kotlin.viaggio.model

import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.ViaggioApiAWSAuth
import com.kotlin.viaggio.data.obj.ViaggioApiAuth
import com.kotlin.viaggio.data.obj.ViaggioResult
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
    lateinit var pref: AndroidPrefUtilService
    @Inject
    lateinit var config: DeveloperAuthenticationProvider
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    private fun getToken() = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID)

    fun signIn(email:String, password:String):Single<Response<ViaggioApiAuth>> =
            api.signIn(email = email, passwordHash = password)
                .doOnSuccess {
                    it.body()?.also {auth ->
                        prefUtilService.putString(AndroidPrefUtilService.Key.TOKEN_ID, auth.token).blockingAwait()
                        prefUtilService.putString(AndroidPrefUtilService.Key.USER_ID, auth.email).blockingAwait()
                        prefUtilService.putString(AndroidPrefUtilService.Key.USER_NAME, auth.name).blockingAwait()
                        pref.putString(AndroidPrefUtilService.Key.AWS_ID, auth.AWS_IdentityId).blockingAwait()
                        pref.putString(AndroidPrefUtilService.Key.AWS_TOKEN, auth.AWS_Token).blockingAwait()
                        config.setInfo(auth.AWS_IdentityId, auth.AWS_Token)
                    }
                }
                .subscribeOn(Schedulers.io())

    fun signUp(name:String, email: String, password: String, password2: String):Single<Response<ViaggioApiAuth>> {
        return api.signUp(name = name, email = email, passwordHash2 = password2, passwordHash = password)
            .doOnSuccess {
                it.body()?.also {auth ->
                    prefUtilService.putString(AndroidPrefUtilService.Key.TOKEN_ID, auth.token).blockingAwait()
                    prefUtilService.putString(AndroidPrefUtilService.Key.USER_ID, auth.email).blockingAwait()
                    prefUtilService.putString(AndroidPrefUtilService.Key.USER_NAME, auth.name).blockingAwait()
                    pref.putString(AndroidPrefUtilService.Key.AWS_ID, auth.AWS_IdentityId).blockingAwait()
                    pref.putString(AndroidPrefUtilService.Key.AWS_TOKEN, auth.AWS_Token).blockingAwait()
                    config.setInfo(auth.AWS_IdentityId, auth.AWS_Token)
                }
            }
            .subscribeOn(Schedulers.io())
    }
    fun getAws(): Single<Response<ViaggioApiAWSAuth>>{
        return api.getAws()
            .doOnSuccess {
                it.body()?.also {auth ->
                    pref.putString(AndroidPrefUtilService.Key.AWS_ID, auth.AWS_IdentityId).blockingAwait()
                    pref.putString(AndroidPrefUtilService.Key.AWS_TOKEN, auth.AWS_Token).blockingAwait()
                    config.setInfo(auth.AWS_IdentityId, auth.AWS_Token)
                }
            }
            .subscribeOn(Schedulers.io())
    }

    fun userProfile(imageName:String):Single<List<String>>{
        return localDataSource.recordImage(arrayOf(imageName))
    }

    fun updateUser(name:String, profileName:String): Single<Response<ViaggioResult>>{
        return api.updateUserName(
            name = name, profileImageName = profileName, profileImageUrl = ""
        ).subscribeOn(Schedulers.io())
    }

    fun updatePassword(oldPassword: String, password: String, password2: String): Single<Response<ViaggioResult>>{
        return api.updateUserPassword(oldPasswordHash = oldPassword, passwordHash = password, passwordHash2 = password2)
            .subscribeOn(Schedulers.io())
    }

    fun logOut(): Single<Response<ViaggioResult>> = api.logOut()
        .subscribeOn(Schedulers.io())
}