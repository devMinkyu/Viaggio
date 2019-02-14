package com.kotlin.viaggio.view.ocr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import io.fotoapparat.result.transformer.scaled
import kotlinx.android.synthetic.main.fragment_ocr_image.*
import java.io.File

class OcrImageFragment:BaseFragment<OcrImageFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentOcrImageBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ocr_image, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel().photoResult.observe(this, Observer {
            it.saveToFile(File(
                activity?.getExternalFilesDir("photos"), "photo.jpg"
            ))
            it.toBitmap(scaled(scaleFactor = 1f)).whenAvailable { bitmapPhoto ->
                bitmapPhoto?.let {bitmapPhotoVal ->
                    context?.let { context ->
                        Glide.with(context)
                            .load(bitmapPhotoVal.bitmap)
                            .into(ocrImage)
                    }
                }
            }
        })
    }

    inner class ViewHandler{
        fun retake(){

        }
        fun upload(){

        }
    }
}