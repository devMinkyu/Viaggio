package com.kotlin.viaggio.view.setting

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.ImageData
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelLocalModel
import com.kotlin.viaggio.model.UserModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class SettingProfileImageEnrollFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelLocalModel: TravelLocalModel
    @Inject
    lateinit var userModel: UserModel

    val imagePathList: MutableLiveData<Event<List<ImageData>>> = MutableLiveData()
    var imageAllList: List<ImageData> = listOf()
    var chooseImage: ObservableField<String> = ObservableField("")

    override fun initialize() {
        super.initialize()
        if (imageAllList.isEmpty()) {
            val result = travelLocalModel.imageAllPath()
                .map {
                    ImageData(imageName = it)
                }
            imageAllList = result
            imagePathList.value = Event(imageAllList)
        }
    }

    fun confirm() {
        rxEventBus.userImage.onNext(chooseImage.get()!!)
    }

}
