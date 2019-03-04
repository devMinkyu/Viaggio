package com.kotlin.viaggio.view.traveling.detail

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_representative_image.*
import java.io.File


class TravelingRepresentativeImageFragment:BaseFragment<TravelingRepresentativeImageFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingRepresentativeImageBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_representative_image, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel().imageNamesListLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let { travelCards ->
                val imgDir = File(context?.filesDir, "images/")
                if (imgDir.exists()) {
                    val imgFile = File(imgDir, travelCards[0].imageNames[0])
                    if (imgFile.exists()) {
                        Uri.fromFile(imgFile).let { uri ->
                            Glide.with(travelingRepresentativeImage)
                                .load(uri)
                                .into(travelingRepresentativeImage)
                        }
                    }
                }

                Log.d("hoho", "$travelCards")
            }
        })

    }

    inner class ViewHandler{
        fun confirm(){

        }
        fun back(){
            fragmentPopStack()
        }
    }
}