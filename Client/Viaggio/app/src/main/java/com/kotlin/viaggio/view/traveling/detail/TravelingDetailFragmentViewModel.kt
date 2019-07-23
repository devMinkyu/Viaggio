package com.kotlin.viaggio.view.traveling.detail

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TravelingDetailFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val dayCount = ObservableInt(0)
    val content = ObservableField("")
    val country = ObservableField("")
    val theme = ObservableField("")
    val date = ObservableField("")

    val imageSize = ObservableInt(0)
    val currentImageSize = ObservableInt(1)
    val imageShow = ObservableBoolean(false)

    val travelOfDayCardImageListLiveData = MutableLiveData<Event<List<String>>>()
    val changeCardLiveData = MutableLiveData<Event<Int>>()
    val completeLiveData = MutableLiveData<Event<Boolean>>()

    var timeDisposable:Disposable? = null
    var travelCard = TravelCard()
    var modifyLocation = IntArray(2)
    override fun initialize() {
        super.initialize()
        fetchData()
        showNotice()

        val changeDisposable = rxEventBus.travelCardChange
            .subscribe {
                changeCardLiveData.value = Event(it)
            }
        addDisposable(changeDisposable)


        val disposable = rxEventBus.travelCardUpdate
            .subscribeOn(Schedulers.io())
            .subscribe({
                fetchData()
            }) {
                Timber.d(it)
            }
        addDisposable(disposable)

        val deleteDisposable = rxEventBus.travelCardDelete
            .subscribe {
                completeLiveData.postValue(Event(false))
                delete()
            }
        addDisposable(deleteDisposable)
    }

    private fun fetchData(){
        val disposable = travelLocalModel.getTravelCard()
            .flatMap { t ->
                if (t.isNotEmpty()) {
                    travelCard = t[0]
                    val item = t[0]
                    theme.set(item.theme.joinToString(", "))
                    dayCount.set(item.travelOfDay)
                    country.set(item.country)
                    content.set(item.content)
                    imageSize.set(item.imageNames.size)
                    travelOfDayCardImageListLiveData.postValue(Event(item.imageNames))
                    travelLocalModel.getTravel(item.travelLocalId)
                }else{
                    Single.just(Travel())
                }
            }
            .observeOn(Schedulers.io())
            .subscribe ({ t ->
                if(t.localId != 0L){
                    val startDate = t.startDate!!
                    val cal = Calendar.getInstance()
                    cal.time = startDate
                    cal.add(Calendar.DATE, dayCount.get() - 1)
                    date.set(DateFormat.getDateInstance(DateFormat.LONG).format(cal.time))
                }
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }

    fun delete(){
        travelCard.isDelete = true
        travelCard.userExist = false
        val disposable = travelLocalModel.updateTravelCard(travelCard)
            .andThen {
                val token = travelLocalModel.getToken()
                val mode = travelLocalModel.getUploadMode()
                if (TextUtils.isEmpty(token).not() && mode != 2 && travelCard.serverId != 0) {
                    deleteWork(travelCard)
                    it.onComplete()
                } else {
                    it.onComplete()
                }
            }
            .subscribe({
                rxEventBus.travelCardUpdate.onNext(Any())
                completeLiveData.postValue(Event(true))
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }
    fun showNotice() {
        val disposable = Completable.timer(2, TimeUnit.SECONDS)
            .subscribe {
                imageShow.set(false)
            }
        timeDisposable = disposable
    }

    fun selectedCountry() {
        prefUtilService.putString(AndroidPrefUtilService.Key.SELECTED_COUNTRY, travelCard.country).blockingAwait()
    }
}
