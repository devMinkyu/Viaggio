package com.kotlin.viaggio.model

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.GoogleSignInBody
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.obj.ViaggioApiAWSAuth
import com.kotlin.viaggio.data.obj.ViaggioApiAuth
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.LocalDataSource
import com.kotlin.viaggio.data.source.ViaggioApiService
import com.kotlin.viaggio.extenstions.imageName
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.io.File
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Singleton
class UserModel @Inject constructor() :BaseModel(){
    @Inject
    lateinit var api: ViaggioApiService
    @Inject
    lateinit var config: DeveloperAuthenticationProvider
    @Inject
    lateinit var pref: AndroidPrefUtilService
    @field:[Inject Named("Application")]
    lateinit var appCtx: Lazy<Context>
    @Inject
    lateinit var transferUtility: TransferUtility

    fun getAwsId():String = pref.getString(AndroidPrefUtilService.Key.AWS_ID).blockingGet()
    fun getAwsToken(): String = pref.getString(AndroidPrefUtilService.Key.AWS_TOKEN).blockingGet()
    fun getUserId(): String = pref.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet()

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
        val completableList = mutableListOf(
            pref.putString(AndroidPrefUtilService.Key.USER_ID, auth.email),
            pref.putString(AndroidPrefUtilService.Key.TOKEN_ID, auth.token),
            pref.putString(AndroidPrefUtilService.Key.USER_NAME, auth.name),
            pref.putBool(AndroidPrefUtilService.Key.GOOGLE_LOGIN, auth.isGoogleId),
            pref.putString(AndroidPrefUtilService.Key.AWS_ID, auth.AWS_IdentityId),
            pref.putString(AndroidPrefUtilService.Key.AWS_TOKEN, auth.AWS_Token),
            pref.putBool(AndroidPrefUtilService.Key.NEW_AWS, true),
            pref.putString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE_URL, if(TextUtils.isEmpty(auth.imageUrl)) "" else auth.imageUrl))
        if(TextUtils.isEmpty(auth.imageUrl).not()) {
            completableList.add(saveUserProfile(auth))
        }
        Completable.merge(completableList).blockingAwait()
    }
    private fun saveUserProfile(auth:ViaggioApiAuth): Completable {
        val awsId = auth.AWS_IdentityId
        val awsToken = auth.AWS_Token
        config.setInfo(awsId, awsToken)

        val imageDir = File(appCtx.get().filesDir, LocalDataSource.IMG_FOLDER)
        if(!imageDir.exists()){
            imageDir.mkdirs()
        }
        return Completable.create { emitter ->
            val imageName = auth.imageUrl.split("/").last()
            val file = File(imageDir, imageName)
            val downloadObserver = transferUtility.download(BuildConfig.S3_UPLOAD_BUCKET, auth.imageUrl, file)
            downloadObserver.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if(state == TransferState.COMPLETED) {
                        pref.putString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE, file.absolutePath).blockingAwait()
                        emitter.onComplete()
                    }
                }
                override fun onError(id: Int, ex: Exception?) {
                    emitter.onError(ex as Throwable)
                }
            })
        }
    }
    fun saveAwsImageToLocal(travelCards: List<TravelCard>):Completable {
        config.setInfo(getAwsId(), getAwsToken())
        val list = travelCards.filter { travelCard ->
            travelCard.imageNames.isNotEmpty()
        }.map {travelCard ->
            travelCard.imageUrl.map {url ->
                Completable.create { emitter ->
                    val imageName = url.split("/").last()
                    val downloadObserver = transferUtility.download(BuildConfig.S3_UPLOAD_BUCKET, url, File(appCtx.get().imageName(imageName)))
                    downloadObserver.setTransferListener(object : TransferListener {
                        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                        override fun onStateChanged(id: Int, state: TransferState?) {
                            if (state == TransferState.COMPLETED) {
                                emitter.onComplete()
                            }
                        }
                        override fun onError(id: Int, ex: Exception?) {
                            emitter.onError(ex as Throwable)
                        }
                    })
                }
            }
        }.flatten()
        return Completable.merge(list)
    }
    fun putAwsImage(imageName: String): Single<String> {
        return Single.create<String> { emitter ->
            config.setInfo(getAwsId(), getAwsToken())
            val uploadObserver = transferUtility.upload(
                BuildConfig.S3_UPLOAD_BUCKET,
                "image/${getUserId()}/${imageName.split("/").last()}",
                File(appCtx.get().imageName(imageName))
            )
            uploadObserver.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if (state == TransferState.COMPLETED) {
                        emitter.onSuccess(uploadObserver.key)
                    }
                }
                override fun onError(id: Int, ex: Exception?) {
                    emitter.onSuccess("")
                }
            })
        }.subscribeOn(Schedulers.io())
    }
}