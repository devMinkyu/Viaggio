package com.kotlin.viaggio.view.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.SignError
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
                .apply(bitmapTransform(BlurTransformation(20, 3)))
                .into(signUpContainer)
        }

        getViewModel().complete.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/")

                fragmentManager?.let {fm->
                    val cnt = fm.backStackEntryCount
                    for(i in 0 until cnt){
                        fm.popBackStackImmediate()
                    }
                }
            }
        })
        getViewModel().error.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let {signError ->
                getViewModel().errorMsg.set(
                    when (signError) {
                        SignError.PW_MISMATCH -> {
                            getString(R.string.err_pw_mismatch)
                        }
                        SignError.INVALID_EMAIL_FORMAT -> {
                            getString(R.string.err_email_format)
                        }
                        SignError.EXIST_EMAIL -> {
                            getString(R.string.err_exist_email)
                        }
                        else -> {null}
                    }
                )
            }?: getViewModel().errorMsg.set(null)
        })
    }

    inner class ViewHandler {
        fun signUp() {
            if(checkInternet()){
                if(getViewModel().validateSignUp()){
                    showLoading()
                }
            }else{

            }
        }
        fun back(){
            fragmentPopStack()
        }
    }
}