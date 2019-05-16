package com.kotlin.viaggio.view.traveling.enroll

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.ImageData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingCardImageEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val imagePathList: MutableLiveData<Event<List<ImageData>>> = MutableLiveData()

    var entireChooseCount: Int = 1

    var imageAllList: List<ImageData> = listOf()
    val imageChooseList: MutableList<String> = mutableListOf()
    var imageBitmapChooseList: MutableList<Bitmap> = mutableListOf()

    override fun initialize() {
        super.initialize()
        val disposable = rxEventBus.travelCacheImages
            .subscribe {
                if(it.isNotEmpty()){
                    imageAllList = it
                    imagePathList.value = Event(imageAllList)
                    val result = imageAllList.filter { imageDataVal ->
                        imageDataVal.chooseCountList.get() != 0
                    }.sortedBy { imageDataVal ->
                        imageDataVal.chooseCountList.get()
                    }.map { imageDataVal ->
                        imageDataVal.imageName
                    }
                    imageChooseList.clear()
                    imageChooseList.addAll(result)
                    entireChooseCount = if (imageChooseList.isEmpty()) {
                        imageChooseList.add(imageAllList[0].imageName)
                        imageAllList[0].chooseCountList.set(1)
                        1
                    } else {
                        imageChooseList.size
                    }
                }
            }
        addDisposable(disposable)

        val bitmapDisposable = rxEventBus.travelCardImages
            .subscribe {
                if(it.isNotEmpty()){
                    imageBitmapChooseList = it.toMutableList()
                    imageBitmapChooseList.remove(imageBitmapChooseList.last())
                }
            }
        addDisposable(bitmapDisposable)

        if (imageAllList.isEmpty()) {
            val result = travelLocalModel.imageAllPath()
                .map {
                    ImageData(imageName = it)
                }
            imageAllList = result
            imageChooseList.add(imageAllList[0].imageName)
            imageAllList[0].chooseCountList.set(1)
            imagePathList.value = Event(imageAllList)
        }
    }

    fun selectImage() {
        rxEventBus.travelCardImages.onNext(imageBitmapChooseList)
        rxEventBus.travelCacheImages.onNext(imageAllList)
    }
}
