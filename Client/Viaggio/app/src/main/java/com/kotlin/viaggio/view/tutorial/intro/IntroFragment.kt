package com.kotlin.viaggio.view.tutorial.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentIntroBinding
import com.kotlin.viaggio.extenstions.baseIntent
import com.kotlin.viaggio.view.common.BaseFragment


class IntroFragment : BaseFragment<IntroFragmentViewModel>() {
    companion object {
        val TAG: String = IntroFragment::class.java.simpleName
    }
    lateinit var binding: FragmentIntroBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false)
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    override fun onResume() {
        super.onResume()
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    override fun onStop() {
        super.onStop()
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

    }
    inner class ViewHandler {
        fun tutorial() {
            baseIntent("http://viaggio.kotlin.com/home/tutorial/")
        }
        fun skip(){
            baseIntent("http://viaggio.kotlin.com/home/main/")
            parentFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}