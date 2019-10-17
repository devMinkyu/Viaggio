package com.kotlin.viaggio.view.main_activity

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
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

    val backButtonSubject: Subject<Long> = BehaviorSubject.createDefault(0L).toSerialized()
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
        fetchDay()
        if(prefUtilService.getBool(AndroidPrefUtilService.Key.FIRST_LOGIN, true).blockingGet()) {
            Completable.mergeArray(
                prefUtilService.putBool(AndroidPrefUtilService.Key.FIRST_LOGIN, false),
                prefUtilService.putInt(AndroidPrefUtilService.Key.IMAGE_MODE, 0),
                prefUtilService.putInt(AndroidPrefUtilService.Key.UPLOAD_MODE, 0)
            )
            .blockingAwait()
            dataFetch()
        }
    }

    private fun fetchDay() {
        val cal = Calendar.getInstance()
        val lastConnectOfDay = prefUtilService.getInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY).blockingGet()
        val currentConnectOfDay = cal.get(Calendar.DAY_OF_MONTH)
        val completableList = mutableListOf<Completable>()
        completableList.add(prefUtilService.putInt(AndroidPrefUtilService.Key.LAST_CONNECT_OF_DAY, currentConnectOfDay))
        if ((currentConnectOfDay - lastConnectOfDay) != 0) {
            val token = prefUtilService.getString(AndroidPrefUtilService.Key.TOKEN_ID).blockingGet()
            val nextReview = prefUtilService.getBool(AndroidPrefUtilService.Key.NEXT_GIVE_REVIEW, false).blockingGet()
            var periodApp = prefUtilService.getInt(AndroidPrefUtilService.Key.PERIOD_APP, 0).blockingGet()?:0
            periodApp += 1
            completableList.add(prefUtilService.putInt(AndroidPrefUtilService.Key.PERIOD_APP, periodApp))

            if(TextUtils.isEmpty(token).not()){
                completableList.add(prefUtilService.putBool(AndroidPrefUtilService.Key.NEW_AWS, false))
                addDisposable(userModel.getAws().subscribe())
            }
            if(nextReview) {
                var nextPeriodApp = prefUtilService.getInt(AndroidPrefUtilService.Key.NEXT_REVIEW_PERIOD_APP, 0).blockingGet()?:0
                nextPeriodApp += 1
                completableList.add(prefUtilService.putInt(AndroidPrefUtilService.Key.NEXT_REVIEW_PERIOD_APP, nextPeriodApp))
            }

            if(traveling) {
                var travelingOfDayOfCount =
                    prefUtilService.getInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT).blockingGet()
                travelingOfDayOfCount += 1
                completableList.add(prefUtilService.putInt(AndroidPrefUtilService.Key.TRAVELING_OF_DAY_COUNT, travelingOfDayOfCount))
            }
        }

        val disposable = Completable.merge(completableList).subscribe()
        addDisposable(disposable)
    }

    fun reviewRequest():LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        val review = prefUtilService.getBool(AndroidPrefUtilService.Key.GIVE_REVIEW, false).blockingGet() ?: false
        if(review) {
            result.value = review
        } else {
            var frequencyApp = prefUtilService.getInt(AndroidPrefUtilService.Key.FREQUENCY_APP, 0).blockingGet()?:0
            val periodApp = prefUtilService.getInt(AndroidPrefUtilService.Key.PERIOD_APP, 0).blockingGet()?:0
            val nextPeriodApp = prefUtilService.getInt(AndroidPrefUtilService.Key.NEXT_REVIEW_PERIOD_APP, 0).blockingGet()?:0
            frequencyApp += 1
            if(frequencyApp > 10 || periodApp > 7 || nextPeriodApp > 3) {
                Completable.mergeArray(
                    prefUtilService.putInt(AndroidPrefUtilService.Key.FREQUENCY_APP, 0),
                    prefUtilService.putInt(AndroidPrefUtilService.Key.PERIOD_APP, 0),
                    prefUtilService.putInt(AndroidPrefUtilService.Key.NEXT_REVIEW_PERIOD_APP, 0)
                )
                .blockingAwait()
                result.value = false
            } else {
                prefUtilService.putInt(AndroidPrefUtilService.Key.FREQUENCY_APP, frequencyApp).blockingAwait()
                result.value = true
            }
        }
        return result
    }

    fun checkTutorial() = prefUtilService.getBool(AndroidPrefUtilService.Key.TUTORIAL_CHECK).blockingGet() ?: false
    fun getLock() = prefUtilService.getBool(AndroidPrefUtilService.Key.LOCK_APP).blockingGet() ?: false
}