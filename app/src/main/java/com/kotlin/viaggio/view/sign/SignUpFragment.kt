package com.kotlin.viaggio.view.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment<SignUpFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSignUpBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { context ->
            Glide.with(context)
                .load(R.drawable.background)
                .apply(bitmapTransform(BlurTransformation(15, 3)))
                .into(signUpContainer)
        }
    }

    inner class ViewHandler {
        fun signIn() {

        }
    }
}