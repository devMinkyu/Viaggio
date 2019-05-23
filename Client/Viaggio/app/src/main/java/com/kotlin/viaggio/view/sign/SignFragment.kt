package com.kotlin.viaggio.view.sign

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentSignBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.fragment_sign.*

class SignFragment : BaseFragment<SignFragmentViewModel>() {

    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                sign_container_view, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
    }
    lateinit var binding: FragmentSignBinding
    override fun onAttach(context: Context) {
        activity!!.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            Glide.with(context)
                .load(R.drawable.background)
                .apply(bitmapTransform(BlurTransformation(20, 1)))
                .into(signContainer)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    inner class ViewHandler{
        fun normalSign(){
            baseIntent("http://viaggio.kotlin.com/login/normal/")
        }
        fun createAccount(){
            baseIntent("http://viaggio.kotlin.com/login/create/")
        }
        fun back(){
            fragmentPopStack()
        }
    }
}