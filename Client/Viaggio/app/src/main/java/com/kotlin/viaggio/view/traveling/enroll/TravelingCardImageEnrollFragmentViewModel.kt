package com.kotlin.viaggio.view.traveling.enroll

import android.graphics.Bitmap
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TravelingCardImageEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel

    val imagePathList: MutableLiveData<Event<MutableList<String>>> = MutableLiveData()

    var entireChooseCount:Int = 1
    val chooseCountList: MutableList<ObservableInt> = mutableListOf()
    val imageAllList: MutableList<String> = mutableListOf()
    val imageChooseList: MutableList<String> = mutableListOf()
    val imageBitmapChooseList: MutableList<Bitmap> = mutableListOf()

    override fun initialize() {
        super.initialize()
        for (s in travelLocalModel.imageAllPath()) {
            imageAllList.add(s)
            chooseCountList.add(ObservableInt(0))
        }
        imageChooseList.add(imageAllList[0])
        chooseCountList[0].set(1)
        imagePathList.value = Event(imageAllList)
    }

    fun selectImage() {
        rxEventBus.travelOfDayImages.onNext(imageBitmapChooseList)
    }

}
