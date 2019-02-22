package com.kotlin.viaggio.view.traveling

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.worker.CompressWorker
import kotlinx.android.synthetic.main.fragment_traveling.*
import org.jetbrains.anko.support.v4.toast


class TravelingFragment : BaseFragment<TravelingFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().goToCamera.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/camera/")
            }

        })
        getViewModel().permissionRequestMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { permissionError ->
                when (permissionError) {
                    PermissionError.NECESSARY_PERMISSION -> toast(resources.getString(R.string.camera_permission))
                    else -> {
                    }
                }
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
                            stopLoading()
                            val images = workInfo.outputData.getStringArray(WorkerName.COMPRESS_IMAGE.name)
                            val uris = mutableListOf<Uri>()
                            images?.let {
                                for (image in images) {
                                    uris.add(Uri.parse(image))
                                    Log.d("hoho", image)
                                }
                            }
                        }
                    })
                }
            }
        })
    }

    inner class ViewHandler{
        fun cameraOpen(){
            getViewModel().permissionCheck(
                rxPermission.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }
}