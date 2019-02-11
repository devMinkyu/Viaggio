package com.kotlin.viaggio.view.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment

class TutorialFragment:BaseFragment<TutorialFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTutorialBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHolder()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    inner class ViewHolder
}