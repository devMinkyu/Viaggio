package com.kotlin.viaggio.view.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentDialogOcrImageActionBinding
import com.kotlin.viaggio.view.common.BaseDialogFragment

class OcrImageActionDialogFragment: BaseDialogFragment<OcrImageActionDialogFragmentViewModel>() {
    companion object {
        val TAG: String = OcrImageActionDialogFragment::class.java.simpleName
    }

    lateinit var binding:FragmentDialogOcrImageActionBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_ocr_image_action, container, false)
        binding.viewModel = getViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}