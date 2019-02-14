package com.kotlin.viaggio.view.camera

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import com.tbruyelle.rxpermissions2.Permission
import io.fotoapparat.result.PhotoResult
import io.reactivex.Observable
import javax.inject.Inject

class CameraFragmentViewModel @Inject constructor():BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    val permissionRequestMsg: MutableLiveData<PermissionError> = MutableLiveData()
    override fun initialize() {
        super.initialize()
    }

    internal fun savePicture(photoResult: PhotoResult) {
        travelModel.cameraResult.onNext(photoResult)
    }
    override fun permissionCheck(request: Observable<Permission>?) {
        super.permissionCheck(request)
        val disposable = request?.subscribe { t ->
            when {
                t.granted -> when(t.name){}
                !t.shouldShowRequestPermissionRationale -> { }
                else -> permissionRequestMsg.value = PermissionError.STORAGE_PERMISSION

            }
        }
        disposable?.let { addDisposable(it) }
    }

}
