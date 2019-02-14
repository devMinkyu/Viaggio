package com.kotlin.viaggio.view.ocr

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.view.common.BaseViewModel
import io.fotoapparat.result.PhotoResult
import javax.inject.Inject

class OcrImageActionDialogFragmentViewModel @Inject constructor() : BaseViewModel() {
    val photoResult:MutableLiveData<PhotoResult> = MutableLiveData()
    override fun initialize() {
        super.initialize()
    }
}
