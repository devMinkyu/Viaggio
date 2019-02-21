package com.kotlin.viaggio.view.camera

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.ScaleAnimation
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.worker.CompressWorker
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.item_camera_image.view.*
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

    @SuppressLint("EnqueueWork")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomSheetBehavior.from(cameraViewImageBottomSheet).state = STATE_HIDDEN

        context?.let {context ->
            cameraViewImageAllList.layoutManager = GridLayoutManager(context, 3)
            Glide.with(context)
                .load(getViewModel().imagePathList[0])
                .into(cameraViewImage)
        }

        getViewModel().permissionRequestMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let {permissionError ->
                when (permissionError) {
                    PermissionError.STORAGE_PERMISSION -> toast(resources.getString(R.string.storage_permission))
                    else -> {
                    }
                }
            }
        })
        getViewModel().photoUri.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { uri ->
                context?.let { contextVal ->
                    Glide.with(contextVal)
                        .load(uri)
                        .into(ocrImage)
                }
            }
        })
        getViewModel().imageViewShow.observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                BottomSheetBehavior.from(cameraViewImageBottomSheet).state = STATE_COLLAPSED
                cameraViewImageAllList.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
                        CameraViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_camera_image, parent, false))
                    override fun getItemCount() = getViewModel().imagePathList.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as CameraViewHolder
                        holder.binding?.viewHandler = holder.CameraViewHandler()
                        holder.imageBinding(getViewModel().imagePathList[position])
                    }
                }
            }
        })

        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                fragmentManager?.let {fragmentManager->
                    ocrLottie.cancelAnimation()
                    OcrImageActionDialogFragment().show(fragmentManager, OcrImageActionDialogFragment.TAG)
                }
            }
        })

        getViewModel().travelingStart.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                getViewModel().cacheImage((ocrImage.drawable as BitmapDrawable).bitmap)
            }
        })
        getViewModel().compressFile.observe(this, Observer {
            it.getContentIfNotHandled()?.let {file ->
                val inputData = Data.Builder().putStringArray(WorkerName.COMPRESS_IMAGE.name, arrayOf(file.absolutePath)).build()
                val compressWork = OneTimeWorkRequestBuilder<CompressWorker>()
                    .setInputData(inputData)
                    .build()
                WorkManager.getInstance().let { work ->
                    work.enqueue(compressWork)

                    work.getWorkInfoByIdLiveData(compressWork.id).observe(this, Observer { workInfo ->
                        if (workInfo != null && workInfo.state.isFinished) {
                            val images = workInfo.outputData.getStringArray(WorkerName.COMPRESS_IMAGE.name)
                            val uris = mutableListOf<Uri>()
                            images?.let {
                                for (image in images) {
                                    uris.add(Uri.parse(image))
                                    Log.d("hoho", image)
                                    fragmentPopStack()
                                }
                            }
                        }
                    })
                }
            }
        })
    }


    fun scaleAnimation():ScaleAnimation{
        val scale = ScaleAnimation(0.95f, 1f, 0.95f, 1f)
        scale.duration = 200
        return scale
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }

    inner class ViewHandler {
        fun shutter() {
            ocrLottie.playAnimation()
            cameraViewShutter.startAnimation(scaleAnimation())
            val photoResult = fotoapparat.autoFocus().takePicture()
            getViewModel().savePicture(photoResult)
        }

        fun close() {
            cameraViewClose.startAnimation(scaleAnimation())
            fragmentPopStack()
        }

        fun imageOpen() {
            cameraViewImage.startAnimation(scaleAnimation())
            getViewModel().permissionCheck(
                rxPermission.request(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
        fun retake(){
            ocrLottie.cancelAnimation()
            getViewModel().isImageMake.set(false)

            // 임의로 한 사진
            ocrImage.setImageDrawable(resources.getDrawable(R.drawable.empty_gallery, null))
        }
    }
    inner class CameraViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemCameraImageBinding>(itemView)
        private lateinit var fileNamePath:String
        fun imageBinding(string: String){
            fileNamePath = string
            binding?.let {
                Glide.with(itemView)
                    .load(string)
                    .into(itemView.cameraViewListImage)
            }
        }

        inner class CameraViewHandler{
            fun imagePicker(){
                ocrLottie.playAnimation()
                getViewModel().isImageMake.set(true)
                BottomSheetBehavior.from(cameraViewImageBottomSheet).state = STATE_HIDDEN
                context?.let {contextVal ->
                    Glide.with(contextVal)
                        .load(fileNamePath)
                        .into(ocrImage)
                }
                getViewModel().visionTextRecognizer(BitmapFactory.decodeFile(fileNamePath))
            }
        }
    }
}


