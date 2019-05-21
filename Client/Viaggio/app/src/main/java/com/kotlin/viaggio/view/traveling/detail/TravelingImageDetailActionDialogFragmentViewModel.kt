package com.kotlin.viaggio.view.traveling.detail

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
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

class TravelingImageDetailActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val completeLiveDate: MutableLiveData<Event<Any>> = MutableLiveData()

    var travelCard = TravelCard()
    val travelOfDayCardImageListLiveData = MutableLiveData<Event<List<String>>>()

    val imageSize = ObservableInt(0)
    val currentImageSize = ObservableInt(1)
    val imageShow = ObservableBoolean(false)

    var choosePosition = 0

    var timeDisposable: Disposable? = null
    override fun initialize() {
        super.initialize()

        val disposable = travelLocalModel.getTravelCard()
            .observeOn(Schedulers.io())
            .subscribe ({ t ->
                if (t.isNotEmpty()) {
                    travelCard = t[0]
                    val item = t[0]
                    imageSize.set(item.imageNames.size)
                    travelOfDayCardImageListLiveData.postValue(Event(item.imageNames))
                    showNotice()
                }
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
}