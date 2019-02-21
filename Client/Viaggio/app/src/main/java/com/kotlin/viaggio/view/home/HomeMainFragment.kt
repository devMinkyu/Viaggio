package com.kotlin.viaggio.view.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_home_main.*
import java.io.File


class HomeMainFragment:BaseFragment<HomeMainFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentHomeMainBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_main, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgDir = File(context?.filesDir, "images/")
        if (imgDir.exists()) {
            val imgFile = File(imgDir, "ttttt.jpg")
            if (imgFile.exists()) {
                test.visibility = View.VISIBLE
                Uri.fromFile(imgFile).let { uri ->
                    Glide.with(test)
                        .load(uri)
                        .into(test)
                }
            }
        }
    }

    inner class ViewHandler{
        fun userProfile(){
            baseIntent("http://viaggio.kotlin.com/setting/main/")
        }
    }
}