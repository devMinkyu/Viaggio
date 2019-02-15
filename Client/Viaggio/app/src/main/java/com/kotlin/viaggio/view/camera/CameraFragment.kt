package com.kotlin.viaggio.view.camera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.IntentName
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.view.common.BaseFragment
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.fragment_camera.*
import org.jetbrains.anko.support.v4.toast


class CameraFragment : BaseFragment<CameraFragmentViewModel>() {
    private lateinit var fotoapparat: Fotoapparat
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentCameraBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        fotoapparat = Fotoapparat
            .with(context!!)
            .into(binding.cameraView)           // view which will draw the camera preview
            .previewScaleType(ScaleType.CenterCrop)  // we want the preview to fill the view
            .photoResolution(highestResolution())   // we want to have the biggest photo possible
            .lensPosition(back())       // we want back camera
            .focusMode(
                firstAvailable(  // (optional) use the first focus mode which is supported by device
                    continuousFocusPicture(),
                    autoFocus(), // in case if continuous focus is not available on device, auto focus will be used
                    fixed()             // if even auto focus is not available - fixed focus mode will be used
                )
            ).build()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetBehavior = BottomSheetBehavior.from(cameraViewImageBottomSheet)
        bottomSheetBehavior.state = STATE_HIDDEN

        context?.let {context ->
            Glide.with(context)
                .load(getViewModel().imagePathList[0])
                .into(cameraViewImage)
        }

        getViewModel().permissionRequestMsg.observe(this, Observer {
            when (it) {
                PermissionError.STORAGE_PERMISSION -> toast(resources.getString(R.string.storage_permission))
                else -> {
                }
            }
        })
        getViewModel().photoUri.observe(this, Observer {
            it?.let { uri ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://viaggio.kotlin.com/home/main/camera/image/")
                )
                intent.putExtra(IntentName.OCR_IMAGE_URI_INTENT.name, uri.toString())
                intent.setPackage(BuildConfig.APPLICATION_ID)
                startActivity(intent)
                getViewModel().photoUri.value = null
            }
        })
        getViewModel().imageViewShow.observe(this, Observer {
            it?.let {
                bottomSheetBehavior.state = STATE_COLLAPSED
                getViewModel().imageViewShow.value = null
            }
        })
    }

    inner class ViewHandler {
        fun shutter() {
            val scale = ScaleAnimation(0.95f, 1f, 0.95f, 1f)
            scale.duration = 200
            cameraViewShutter.startAnimation(scale)

            val photoResult = fotoapparat.autoFocus().takePicture()
            getViewModel().savePicture(photoResult)
        }

        fun close() {
            val scale = ScaleAnimation(0.95f, 1f, 0.95f, 1f)
            scale.duration = 200
            cameraViewClose.startAnimation(scale)
            fragmentPopStack()
        }

        fun imageOpen() {
            getViewModel().permissionCheck(
                rxPermission.request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }
}