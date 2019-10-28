package com.kotlin.viaggio.view.setting

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.aws.DeveloperAuthenticationProvider
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SettingFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var userModel: UserModel
    @Inject
    lateinit var transferUtility: TransferUtility
    @Inject
    lateinit var config: DeveloperAuthenticationProvider

    val name = ObservableField<String>("")
    val email = ObservableField<String>("")
    val isLogin = ObservableBoolean(false)
    val appVersion = ObservableField<String>("")
    val lockNotice = ObservableBoolean(false)
    var imageName: MutableLiveData<Event<String>> = MutableLiveData()

    val checkLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showDialogLiveData: MutableLiveData<Event<Any>> = MutableLiveData()
    val synchronizedStart: MutableLiveData<Event<Any>> = MutableLiveData()

    val travels = mutableListOf<Travel>()
    val travelCards = mutableListOf<TravelCard>()
    var localSync = false
    override fun initialize() {
        super.initialize()
        appVersion.set(BuildConfig.VERSION_NAME)
        if (TextUtils.isEmpty(prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()).not()) {
            isLogin.set(true)
            email.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet())
            name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
            imageName.value =
                Event(prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE).blockingGet())
        }

        val disposable = rxEventBus.userUpdate
            .subscribe {
                if (TextUtils.isEmpty(prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet())) {
                    isLogin.set(false)
                } else {
                    name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
                    imageName.postValue(Event(prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE).blockingGet()))
                }
            }
        addDisposable(disposable)

        val uploadCheckDisposable = rxEventBus.uploadCheck
            .subscribe { value ->
                if (value) {
                    showDialogLiveData.value = Event(Any())
                    check()
                }
            }
        addDisposable(uploadCheckDisposable)
        val syncDisposable = rxEventBus.sync
            .subscribe {
                if (it) {
                    synchronizedStart.value = Event(Any())
                }
            }
        addDisposable(syncDisposable)

        lockNotice.set(prefUtilService.getBool(AndroidPrefUtilService.Key.LOCK_APP).blockingGet())
        val lockDisposable = rxEventBus.completeLock
            .subscribe {
                lockNotice.set(prefUtilService.getBool(AndroidPrefUtilService.Key.LOCK_APP).blockingGet())
            }
        addDisposable(lockDisposable)
    }

    fun check() {
        val travelSingle = travelLocalModel.getNotUploadTravels()
        val travelCardSingle = travelLocalModel.getNotUploadTravelCards()

        val disposable = Single.zip(travelSingle, travelCardSingle, BiFunction
        <List<Travel>, List<TravelCard>, Boolean>
        { t1, t2 ->
            travels.clear()
            travels.addAll(t1)
            travelCards.clear()
            travelCards.addAll(t2)
            val list1 = t1.filter { it.userExist.not() }
            val list2 = t2.filter { it.userExist.not() }
            !(list1.isNotEmpty() || list2.isNotEmpty())
        }).subscribeOn(Schedulers.io())
            .flatMap {
                localSync = it
                if (it) {
                    travelModel.sync()
                        .flatMap { response ->
                            if (response.isSuccessful) {
                                if ((response.body()!!.travelCount == travels.filter { travel -> travel.isDelete.not() }.size) &&
                                    (response.body()!!.travelCardCount == travelCards.filter { travelCard -> travelCard.isDelete.not() }.size)
                                ) {
                                    Single.just(true)
                                } else {
                                    Single.just(false)
                                }
                            } else {
                                Single.just(false)
                            }
                        }
                } else {
                    Single.just(it)
                }
            }
            .subscribe({
                checkLiveData.postValue(Event(it))
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)
    }

    fun downData(): LiveData<Event<Any>> {
    val completeLiveData: MutableLiveData<Event<Any>> = MutableLiveData()
        val disposable = downloadData()
            .subscribe({
                completeLiveData.postValue(Event(Any()))
                rxEventBus.travelUpdate.onNext(Any())
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)
    return completeLiveData
    }

    private fun downloadData(): Completable {
        val dt = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        dt.timeZone = TimeZone.getTimeZone("GMT")
        return travelLocalModel.clearTravel()
            .andThen {
                syncDataFetch()
                travelModel.getTravels()
                    .flatMapCompletable { t1 ->
                        val list =
                            if (t1.isNotEmpty()) {
                                t1.map { travelBody ->
                                    if (travelBody.endDate == null) {
                                        prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, true)
                                            .blockingAwait()
                                        prefUtilService.putLong(
                                            AndroidPrefUtilService.Key.TRAVELING_ID,
                                            travelBody.localId
                                        ).blockingAwait()
                                        val cal = Calendar.getInstance()
                                        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
                                        prefUtilService.putInt(
                                            AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY,
                                            currentConnectOfDay
                                        ).blockingAwait()
                                        prefUtilService.putString(
                                            AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES,
                                            "${travelBody.area[0].country}_${travelBody.area[0].city}"
                                        ).blockingAwait()
                                        prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 1)
                                            .blockingAwait()
                                    }
                                    Travel(
                                        localId = travelBody.localId,
                                        serverId = travelBody.serverId,
                                        area = travelBody.area,
                                        theme = travelBody.theme,
                                        title = travelBody.title,
                                        imageUrl = travelBody.imageUrl,
                                        imageName = travelBody.imageName,
                                        travelKind = travelBody.travelKind,
                                        startDate = dt.parse(travelBody.startDate),
                                        endDate = travelBody.endDate?.let { endDate -> dt.parse(endDate) },
                                        share = travelBody.share,
                                        isDelete = travelBody.isDelete,
                                        userExist = true
                                    )
                                }
                            } else {
                                listOf()
                            }
                        if (list.isNotEmpty()) {
                            travelLocalModel.createTravels(*list.toTypedArray())
                        } else {
                            Completable.complete()
                        }
                    }.blockingAwait()
                travelModel.getTravelCards()
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { t2 ->
                        val list2 = if (t2.isNotEmpty()) {
                            t2.map { travelCardBody ->
                                TravelCard(
                                    localId = travelCardBody.localId,
                                    serverId = travelCardBody.serverId,
                                    travelLocalId = travelCardBody.travelLocalId,
                                    travelServerId = travelCardBody.travelServerId,
                                    theme = travelCardBody.theme,
                                    content = travelCardBody.content,
                                    country = travelCardBody.country,
                                    date = dt.parse(travelCardBody.date),
                                    time = dt.parse(if(TextUtils.isEmpty(travelCardBody.time)) travelCardBody.date else travelCardBody.time),
                                    userExist = true,
                                    isDelete = travelCardBody.isDelete,
                                    imageNames = travelCardBody.imageNames,
                                    imageUrl = travelCardBody.imageUrl,
                                    travelOfDay = travelCardBody.travelOfDay
                                )
                            }
                        } else {
                            listOf()
                        }
                        if (list2.isNotEmpty()) {
                            val c0 = userModel.saveAwsImageToLocal(list2)
                            val c2 = travelLocalModel.createTravelCard(*list2.toTypedArray())
                            Completable.merge(listOf(c0, c2))
                        } else {
                            Completable.complete()
                        }
                    }.blockingAwait()
                it.onComplete()
            }
    }

    fun getTraveling() = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
}
