package com.kotlin.viaggio.view.traveling.image

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.PermissionError
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class TravelCardImageModifyFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val list:MutableList<String> = mutableListOf()
    val newImageList:MutableList<String> = mutableListOf()

    val imageCount = ObservableField("")
    val imageRemainderCount = ObservableField("")

    val imageNamesListLiveDate: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val imageLiveData:MutableLiveData<Event<List<Any>>> = MutableLiveData()

    val imageList = mutableListOf<Bitmap>()
    override fun initialize() {
        super.initialize()
        imageLiveData.postValue(Event(listOf()))
        val imageDisposable = rxEventBus.travelCardImages
            .subscribeOn(Schedulers.io())
            .subscribe {
                imageLiveData.postValue(Event(it))
                imageList.clear()
                imageList.addAll(it)

                imageCount.set("${imageList.size}")
            }
        addDisposable(imageDisposable)

        val disposable = travelLocalModel.getTravelCard()
            .subscribeOn(Schedulers.io())
            .subscribe({travelCards ->
                val item = travelCards.firstOrNull()
                item?.let {
                    list.addAll(item.imageNames)
                }
                imageRemainderCount.set(String.format(appCtx.get().getString(R.string.travel_card_image_modify_total_count), 20 - list.size))
                imageCount.set("0")
                imageNamesListLiveDate.postValue(Event(list))
            }){
                Timber.d(it)
            }
        addDisposable(disposable)
    }


    fun permissionCheck(request: Observable<Boolean>?):LiveData<Boolean> {
        val imageViewShow: MutableLiveData<Boolean> = MutableLiveData()
        val disposable = request?.subscribe { t ->
            imageViewShow.value = t
            if(t) {
                rxEventBus.travelCardImageModifyCount.onNext(list.size)
            }
        }
        disposable?.let { addDisposable(it) }
        return imageViewShow
    }
}
