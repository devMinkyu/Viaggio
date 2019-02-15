package com.kotlin.viaggio.view.camera

import android.net.Uri
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
    val photoUri:MutableLiveData<Uri?> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<PermissionError> = MutableLiveData()

    fun savePicture(photoResult: PhotoResult) {
        val disposable = travelModel.savePicture(photoResult).subscribe { t1 ->
            photoUri.value = t1
        }
        addDisposable(disposable)
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
