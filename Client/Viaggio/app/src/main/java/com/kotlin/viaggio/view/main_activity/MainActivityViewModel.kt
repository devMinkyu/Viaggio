package com.kotlin.viaggio.view.main_activity

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.android.TimeHelper
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import io.reactivex.subscribers.DefaultSubscriber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainActivityViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var timeHelper: TimeHelper

    val finishActivity:MutableLiveData<Event<Any>> = MutableLiveData()
    val showToast:MutableLiveData<Event<Any>> = MutableLiveData()

    override fun initialize() {
        super.initialize()
        // test
        var test = prefUtilService.putBool(AndroidPrefUtilService.Key.TRAVELING, false).subscribe()
        addDisposable(test)


        val traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
        if (traveling) {
            timeHelper.timeCheckOfDay()
        }

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
    }

    val backButtonSubject: Subject<Long> =
        BehaviorSubject.createDefault(0L)
            .toSerialized()

    fun checkTutorial() = prefUtilService.getBool(AndroidPrefUtilService.Key.TUTORIAL_CHECK).blockingGet() ?: false
}