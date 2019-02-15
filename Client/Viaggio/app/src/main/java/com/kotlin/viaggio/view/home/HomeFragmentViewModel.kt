package com.kotlin.viaggio.view.home

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Observable
import javax.inject.Inject

class HomeFragmentViewModel @Inject constructor() : BaseViewModel() {
    val goToCamera: MutableLiveData<Any?> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<PermissionError?> = MutableLiveData()

    override fun initialize() {
        super.initialize()
    }

    fun permissionCheck(request: Observable<Boolean>?) {
        val disposable = request?.subscribe { t ->
            if(t){
                goToCamera.value = Any()
            }else{
                PermissionError.NECESSARY_PERMISSION
            }
        }
        disposable?.let { addDisposable(it) }
    }
}
