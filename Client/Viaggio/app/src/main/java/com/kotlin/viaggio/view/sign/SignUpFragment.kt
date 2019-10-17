package com.kotlin.viaggio.view.sign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.SignError
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment<SignUpFragmentViewModel>() {
    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                sign_container, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
    }

    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSignUpBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().complete.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/")
                parentFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        })
        getViewModel().error.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let { signError ->
                when (signError) {
                    SignError.PW_MISMATCH -> {
                        getViewModel().confirmPassword.set("")
                        signUpConfirmPasswordEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red, null))
                        signUpConfirmPasswordEdit.error = getString(R.string.err_pw_mismatch)
                    }
                    SignError.PW_NUM -> {
                        getViewModel().password.set("")
                        signUpPasswordEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red, null))
                        signUpPasswordEdit.error = getString(R.string.err_password_num)
                    }
                    SignError.INVALID_EMAIL_FORMAT -> {
                        getViewModel().email.set("")
                        signUpEmailEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red, null))
                        signUpEmailEdit.error = getString(R.string.err_email_format)
                    }
                    SignError.EXIST_EMAIL -> {
                        getViewModel().email.set("")
                        signUpEmailEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red, null))
                        signUpEmailEdit.error = getString(R.string.err_exist_email)
                    }
                    else -> {}
                }
            }
        })
    }

    inner class ViewHandler {
        fun signUp() {
            if (checkInternet()) {
                if (getViewModel().validateSignUp()) {
                    showLoading()
                }
            } else {
                showNetWorkError()
            }
        }

        fun back() {
            fragmentPopStack()
        }
    }
}