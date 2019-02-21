package com.kotlin.viaggio.view.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.event.RxEventBus
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import io.fotoapparat.result.PhotoResult
import io.reactivex.Observable
import java.io.File
import javax.inject.Inject

class CameraFragmentViewModel @Inject constructor():BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var firebaseVision: FirebaseVisionTextRecognizer
    @Inject
    lateinit var rxEventBus: RxEventBus

    val photoUri:MutableLiveData<Event<Uri>> = MutableLiveData()
    val compressFile:MutableLiveData<Event<File>> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    val imageViewShow:MutableLiveData<Event<Any>> = MutableLiveData()
    val travelingStart:MutableLiveData<Event<Any>> = MutableLiveData()
    val complete:MutableLiveData<Event<Any>> = MutableLiveData()

    val isImageMake:ObservableBoolean = ObservableBoolean(false)
    lateinit var imagePathList: MutableList<String>

    override fun initialize() {
        super.initialize()
        imagePathList = travelModel.imageAllPath()

        val disposable = rxEventBus.travelOfGo.subscribe {
            val traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
            if(traveling){
                travelingStart.value = Event(Any())
            }
        }
        addDisposable(disposable)
    }

    fun savePicture(photoResult: PhotoResult) {
        isImageMake.set(true)
        val disposable = travelModel.savePicture(photoResult).subscribe { t1 ->
            photoUri.value = Event(t1)
            visionTextRecognizer(t1)
        }
        addDisposable(disposable)
    }
    fun permissionCheck(request: Observable<Boolean>?) {
        val disposable = request?.subscribe { t ->
            if(t){
                imageViewShow.value = Event(Any())
            }else{
                permissionRequestMsg.value = Event(PermissionError.STORAGE_PERMISSION)
            }
        }
        disposable?.let { addDisposable(it) }
    }
    fun cacheImage(bitmap: Bitmap){
        val disposable = travelModel.cacheImage(bitmap).subscribe { t1 ->
             compressFile.postValue(Event(t1))
        }
        addDisposable(disposable)
    }
    fun visionTextRecognizer(any: Any){
        when(any){
            is Bitmap ->{
                firebaseVision.processImage(FirebaseVisionImage.fromBitmap(any))
            }
            is Uri ->{
                firebaseVision.processImage(FirebaseVisionImage.fromFilePath(appCtx.get(), any))
            }
            else ->{
                firebaseVision.processImage(FirebaseVisionImage.fromFilePath(appCtx.get(), any as Uri))
            }
        }.addOnCompleteListener {
            complete.value = Event(Any())
            rxEventBus.travelOfCountry.onNext("미국")
        }.addOnFailureListener {}
    }
}
