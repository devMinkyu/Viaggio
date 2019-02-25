package com.kotlin.viaggio.view.traveling

import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.Theme
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject

class TravelingFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var gson: Gson
    val goToCamera: MutableLiveData<Event<Any>> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    val compressFile: MutableLiveData<Event<File>> = MutableLiveData()

    var ticketImage:Bitmap? = null

    var traveling = ObservableBoolean(false)
    val travelThemeList = ObservableArrayList<String>()

    override fun initialize() {
        super.initialize()
        traveling.set(prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING, false).blockingGet())
        val disposable= rxEventBus.travelOfFirstImage
            .observeOn(Schedulers.io())
            .subscribe { t->
                if(!traveling.get()){
                    ticketImage = t
                    cacheImage()
                }
            }
        addDisposable(disposable)

    }
    fun permissionCheck(request: Observable<Boolean>?) {
        val disposable = request?.subscribe { t ->
            if(t){
                goToCamera.value = Event(Any())
            }else{
                permissionRequestMsg.value = Event(PermissionError.NECESSARY_PERMISSION)
            }
        }
        disposable?.let { addDisposable(it) }
    }
    fun cacheImage(){
        ticketImage?.let {ticketImage ->
            val disposable = travelModel.cacheImage(ticketImage).subscribe { t1 ->
                compressFile.postValue(Event(t1))
            }
            addDisposable(disposable)
        }
    }
}
