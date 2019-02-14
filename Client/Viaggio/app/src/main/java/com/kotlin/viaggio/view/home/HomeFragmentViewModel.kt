package com.kotlin.viaggio.view.home

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.view.common.BaseViewModel
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.Observable
import javax.inject.Inject

class HomeFragmentViewModel @Inject constructor() : BaseViewModel() {
    val goToCamera: MutableLiveData<Any> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<PermissionError> = MutableLiveData()

    override fun initialize() {
        super.initialize()
    }

    override fun permissionCheck(request: Observable<Permission>?) {
        super.permissionCheck(request)
        val disposable = request?.subscribe { t ->
            when {
                t.granted -> goToCamera.value = Any()
                !t.shouldShowRequestPermissionRationale -> {}
                else -> permissionRequestMsg.value = PermissionError.CAMERA_PERMISSION
            }
        }
        disposable?.let { addDisposable(it) }
    }
}
