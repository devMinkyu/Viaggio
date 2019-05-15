package com.kotlin.viaggio.view.main_activity

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivityViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var userModel: UserModel

    val finishActivity:MutableLiveData<Event<Any>> = MutableLiveData()
    val showToast:MutableLiveData<Event<Any>> = MutableLiveData()
    var traveling = false
    var travelType = 0
    override fun initialize() {
        super.initialize()
        traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
        val disposable = backButtonSubject.toFlowable(BackpressureStrategy.BUFFER)
            .observeOn(AndroidSchedulers.mainThread())
            .buffer(2, 1) // List<Long>
            .map { Pair<Long,Long>(it[0], it[1]) }
            .map { t ->  t.second - t.first < TimeUnit.SECONDS.toMillis(2)}
            .subscribe { finish ->
                if(finish){
                    finishActivity.value = Event(Any())
                }else{
                    showToast.value = Event(Any())
                }
            }
        addDisposable(disposable)

        val cal = Calendar.getInstance()
        val lastConnectOfDay = prefUtilService.getInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY).blockingGet()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        val token = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()
        if ((currentConnectOfDay - lastConnectOfDay) != 0) {
            if(TextUtils.isEmpty(token).not()){
                addDisposable(userModel.getAws().subscribe())
            }
        }
    }

    val backButtonSubject: Subject<Long> =
        BehaviorSubject.createDefault(0L)
            .toSerialized()

    fun checkTutorial() = prefUtilService.getBool(AndroidPrefUtilService.Key.TUTORIAL_CHECK).blockingGet() ?: false
    fun initSetting() {
        prefUtilService.putInt(AndroidPrefUtilService.Key.IMAGE_MODE, 0).blockingAwait()
        prefUtilService.putInt(AndroidPrefUtilService.Key.UPLOAD_MODE, 0).blockingAwait()
    }
}