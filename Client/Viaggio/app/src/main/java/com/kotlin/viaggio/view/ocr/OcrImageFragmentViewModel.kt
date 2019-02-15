package com.kotlin.viaggio.view.ocr

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class OcrImageFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel
    @Inject
    lateinit var firebaseVision: FirebaseVisionTextRecognizer

    lateinit var uriString:String
    val photoUri:MutableLiveData<Uri> = MutableLiveData()
    override fun initialize() {
        super.initialize()
        photoUri.value = Uri.parse(uriString)
    }
}
