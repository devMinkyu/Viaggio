package com.kotlin.viaggio.view.setting

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.ImageData
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

    var imageAllList: List<ImageData> = listOf()
    var chooseImage: ObservableField<String> = ObservableField("")

    val folderNameListLiveData: MutableLiveData<Event<List<String>>> = MutableLiveData()
    val imagePathList: MutableLiveData<Event<List<ImageData>>> = MutableLiveData()

    val emptyImageNotice = ObservableField<String>()

    override fun initialize() {
        super.initialize()
        folderName()
    }
    private fun folderName() {
        val folderName = travelLocalModel.folderName()
        folderName.add(0, appCtx.get().getString(R.string.total_image))
        folderNameListLiveData.value = Event(folderName)
    }
    private fun imageAllFetch() {
        if (imageAllList.isEmpty()) {
            val result = travelLocalModel.imageAllPath()
                .map {
                    ImageData(imageName = it)
                }
            imageAllList = result
            if(imageAllList.isNullOrEmpty()) {
                emptyImageNotice.set(appCtx.get().resources.getString(R.string.empty_image))
            } else {
                imagePathList.value = Event(imageAllList)
            }
        }
    }

    fun fetchImage(folder: String) {
        emptyImageNotice.set("")
        if(folder == appCtx.get().getString(R.string.total_image)) {
            imageAllFetch()
            imagePathList.value = Event(imageAllList)
        } else {
            val list = imageAllList.filter {
                it.imageName.contains(folder)
            }
            if(list.isNullOrEmpty()) {
                emptyImageNotice.set(appCtx.get().resources.getString(R.string.empty_image))
            }
            imagePathList.value = Event(list)
        }
    }

    fun confirm() {
        rxEventBus.userImage.onNext(chooseImage.get()!!)
    }

}
