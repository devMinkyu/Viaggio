package com.kotlin.viaggio.view.traveling.image

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.PermissionError
import com.kotlin.viaggio.data.obj.TravelCard
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class TravelCardImageModifyFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    lateinit var travelCard:TravelCard

    val list:MutableList<String> = mutableListOf()
    val imageList = mutableListOf<Bitmap>()
    val deleteImageList = mutableListOf<String>()

    val imageCount = ObservableField("")
    val imageRemainderCount = ObservableField("")

    val imageNamesListLiveDate: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()
    val imageLiveData:MutableLiveData<Event<List<Any>>> = MutableLiveData()

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
                    travelCard = it
                    travelCard.newImageNames = mutableListOf()
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

    fun deleteImage(fileNamePath:String):LiveData<Int> {
        val result = MutableLiveData<Int>()
        deleteImageList.add(fileNamePath)
        val index = list.indexOf(fileNamePath)
        list.removeAt(index)

        imageRemainderCount.set(String.format(appCtx.get().getString(R.string.travel_card_image_modify_total_count), 20 - list.size))

        result.value = index
        return result
    }

    fun save(): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        deleteImageList.map {
            File(it).delete()
        }
        val disposable = travelLocalModel.imagePathList(imageList)
            .flatMapCompletable {
                travelCard.newImageNames = it.toMutableList()
                list.addAll(it)
                travelCard.imageNames = list
                travelCard.userExist = false
                travelLocalModel.updateTravelCard(travelCard)
            }
            .andThen {
                val token = travelLocalModel.getToken()
                val mode = travelLocalModel.getUploadMode()
                if (TextUtils.isEmpty(token).not() && mode != 2 && travelCard.serverId != 0) {
                    updateWork(travelCard)
                    it.onComplete()
                } else {
                    it.onComplete()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                rxEventBus.travelCardUpdate.onNext(Any())
                result.postValue(true)
            }) {
                result.postValue(false)
                Timber.d(it)
            }
        addDisposable(disposable)

        return result
    }

    override fun onCleared() {
        super.onCleared()
        rxEventBus.travelCacheImages.onNext(listOf())
        rxEventBus.travelCardImages.onNext(listOf())
    }
}
