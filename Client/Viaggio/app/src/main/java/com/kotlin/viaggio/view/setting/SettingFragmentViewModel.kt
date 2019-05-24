package com.kotlin.viaggio.view.setting

import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.obj.ViaggioApiTravelCards
import com.kotlin.viaggio.data.obj.ViaggioApiTravels
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SettingFragmentViewModel @Inject constructor() : BaseViewModel(){
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var userModel: UserModel

    val name = ObservableField<String>("")
    val email = ObservableField<String>("")
    val isLogin = ObservableBoolean(false)
    val appVersion = ObservableField<String>("")
    val lockNotice = ObservableBoolean(false)
    var imageName:MutableLiveData<Event<String>> = MutableLiveData()

    val checkLiveData:MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showDialogLiveData:MutableLiveData<Event<Any>> = MutableLiveData()
    val completeLiveData:MutableLiveData<Event<Any>> = MutableLiveData()

    val travels = mutableListOf<Travel>()
    val travelCards = mutableListOf<TravelCard>()
    var localSync = false
    override fun initialize() {
        super.initialize()
        appVersion.set(BuildConfig.VERSION_NAME)
        if(TextUtils.isEmpty(prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()).not()){
            isLogin.set(true)
            email.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_ID).blockingGet())
            name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
            imageName.value = Event(prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE).blockingGet())
        }

        val disposable = rxEventBus.userUpdate
            .subscribe {
                if(TextUtils.isEmpty(prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet())){
                    isLogin.set(false)
                }else{
                    name.set(prefUtilService.getString(AndroidPrefUtilService.Key.USER_NAME).blockingGet())
                    imageName.postValue(Event(prefUtilService.getString(AndroidPrefUtilService.Key.USER_IMAGE_PROFILE).blockingGet()))
                }
            }
        addDisposable(disposable)

        val uploadCheckDisposable = rxEventBus.uploadCheck
            .subscribe {value ->
                if(value){
                    showDialogLiveData.value = Event(Any())
                    check()
                }
            }
        addDisposable(uploadCheckDisposable)
        val syncDisposable = rxEventBus.sync
            .subscribe {
                if(it) {
                    showDialogLiveData.value = Event(Any())
                    sync()
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
                        .flatMap {response ->
                            if(response.isSuccessful) {
                                if((response.body()!!.travelCount == travels.filter { travel ->  travel.isDelete.not() }.size) &&
                                    (response.body()!!.travelCardCount == travelCards.filter { travelCard ->  travelCard.isDelete.not() }.size)){
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

    fun sync() {
        prefUtilService.putBool(AndroidPrefUtilService.Key.SYNCING, true).blockingAwait()
        if(localSync) {
            travelModel.getTravels()
                .subscribe { t1, t2 ->
                    if(t1.isSuccessful){
                        Log.d("hoho", "${t1.body()}")
                    }
                }
//            val disposable = travelLocalModel.clearTravel()
//                .subscribeOn(Schedulers.io())
//                .andThen{
//                    val travelSingle = travelModel.getTravels()
//                    val travelCardSingle = travelModel.getTravelCards()
//                    Single.zip(travelSingle, travelCardSingle, BiFunction
//                    <Response<ViaggioApiTravels>, Response<ViaggioApiTravelCards>, List<Any>>
//                    { t1, t2 ->
//                        val list = if(t1.isSuccessful) {
//                            t1.body()!!.travel.map {travel ->
//                                travel.userExist = true
//                                if(travel.endDate == null) {
//                                    prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, true).blockingAwait()
//                                    prefUtilService.putLong(AndroidPrefUtilService.Key.TRAVELING_ID, travel.localId).blockingAwait()
//                                    val cal = Calendar.getInstance()
//                                    val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
//                                    prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay).blockingAwait()
//                                    prefUtilService.putString(AndroidPrefUtilService.Key.TRAVELING_LAST_COUNTRIES, "${travel.area[0].country}_${travel.area[0].city}").blockingAwait()
//                                    prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, 1).blockingAwait()
//                                }
//                                travel
//                            }
//                        } else {
//                            listOf()
//                        }
//
//                        val list2 = if(t2.isSuccessful) {
//                            t2.body()!!.travelCard.map { travelCard ->
//                                travelCard.userExist = true
//                                travelCard
//                            }
//                        } else {
//                            listOf()
//                        }
//                        listOf(list, list2)
//                    }).flatMapCompletable {
//                        val serverTravels = it[0] as List<Travel>
//                        val serverTravelCards = it[1] as List<TravelCard>
//
//                        val c0 = travelLocalModel.saveAwsImageToLocal(serverTravelCards)
//                        val c1 = travelLocalModel.createTravels(*serverTravels.toTypedArray())
//                        val c2 = travelLocalModel.createTravelCard(*serverTravelCards.toTypedArray())
//                        Completable.merge(listOf(c0,c1,c2))
//                    }
//                }.observeOn(Schedulers.io())
//                .subscribe({
//                    completeLiveData.postValue(Event(Any()))
//                }) {
//                    Timber.d(it)
//                }
//            addDisposable(disposable)
        } else {
            val createTravelList = travels
                .filter { it.userExist.not() && it.serverId == 0 }
            val updateTravelList = travels
                .filter { it.userExist.not() && it.serverId != 0 }

            val updateTravelCardList = travelCards
                .filter { it.userExist.not() && it.serverId != 0 }

            // 로컬부터 데이터 다 올려야 함
        }
    }
    fun getTraveling() = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
}
