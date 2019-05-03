package com.kotlin.viaggio.view.traveling.detail

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TravelingDetailFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val dayCount = ObservableInt(0)
    val content = ObservableField<String>("")
    val country = ObservableField<String>("")
    val theme = ObservableField<String>("")
    val date = ObservableField<String>("")

    val imageSize = ObservableInt(0)
    val currentImageSize = ObservableInt(1)
    val imageShow = ObservableBoolean(false)

    val travelOfDayCardImageListLiveData = MutableLiveData<Event<List<String>>>()

    var timeDisposable:Disposable? = null
    override fun initialize() {
        super.initialize()
        fetchData()
        showNotice()
    }

    private fun fetchData(){
        val disposable = travelLocalModel.getTravelCard()
            .flatMap { t ->
                if (t.isNotEmpty()) {
                    val item = t[0]
                    theme.set(item.theme.joinToString(", "))
                    dayCount.set(item.travelOfDay)
                    country.set(item.country)
                    content.set(item.content)
                    imageSize.set(item.imageNames.size)
                    travelOfDayCardImageListLiveData.postValue(Event(item.imageNames))
                    travelLocalModel.getTravel(item.travelId)
                }else{
                    Single.just(Travel())
                }
            }
            .observeOn(Schedulers.io())
            .subscribe ({ t ->
                if(t.id != 0L){
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

    fun showNotice() {
        val disposable = Completable.timer(2, TimeUnit.SECONDS)
            .subscribe {
                imageShow.set(false)
            }
        timeDisposable = disposable
    }
}
