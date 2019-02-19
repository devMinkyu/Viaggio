package com.kotlin.viaggio.view.traveling

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.event.Event
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Observable
import javax.inject.Inject

class TravelingFragmentViewModel @Inject constructor() : BaseViewModel() {
    val goToCamera: MutableLiveData<Event<Any>> = MutableLiveData()
    val permissionRequestMsg: MutableLiveData<Event<PermissionError>> = MutableLiveData()
    var traveling = false
    override fun initialize() {
        super.initialize()
        traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING, false).blockingGet()
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
}
