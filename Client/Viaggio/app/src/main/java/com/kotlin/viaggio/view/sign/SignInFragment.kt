package com.kotlin.viaggio.view.sign

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.SignError
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.common.NetworkDialogFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : BaseFragment<SignInFragmentViewModel>() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }
    override fun onResume() {
        super.onResume()
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                sign_in_container, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
    }
    override fun onStop() {
        super.onStop()
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }

    lateinit var binding: com.kotlin.viaggio.databinding.FragmentSignInBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)
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
                .into(signInContainer)
        }

        getViewModel().complete.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/")

                fragmentManager?.let { fm ->
                    val cnt = fm.backStackEntryCount
                    for (i in 0 until cnt) {
                        fm.popBackStackImmediate()
                    }
                }
            }
        })
        getViewModel().error.observe(this, Observer {
            stopLoading()
            it.getContentIfNotHandled()?.let { signError ->
                signInEmailEdit.setHintTextColor(ResourcesCompat.getColor(resources, R.color.very_light_pink,null))
                signInPasswordEdit.setHintTextColor(ResourcesCompat.getColor(resources, R.color.very_light_pink,null))
                when (signError) {
                    SignError.EMAIL_NOT_FOUND -> {
                        getViewModel().email.set("")
                        signInEmailEdit.setHintTextColor(ResourcesCompat.getColor(resources, R.color.light_red,null))
                        signInEmailEdit.hint = getString(R.string.err_email_not_found)
                    }
                    SignError.WRONG_PW -> {
                        getViewModel().password.set("")
                        signInPasswordEdit.setHintTextColor(ResourcesCompat.getColor(resources, R.color.light_red,null))
                        signInPasswordEdit.hint = getString(R.string.err_wrong_pw)
                    }
                    SignError.DELETE_ID -> {
                        getViewModel().email.set("")
                        signInEmailEdit.setHintTextColor(ResourcesCompat.getColor(resources, R.color.light_red,null))
                        signInEmailEdit.hint = getString(R.string.err_delete_id)
                    }
                    else -> {}
                }
            }
        })
    }

    inner class ViewHandler {
        fun signIn() {
            if (checkInternet()) {
                getViewModel().validateSignIn()
                showLoading()
            } else {
                NetworkDialogFragment().show(fragmentManager!!, NetworkDialogFragment.TAG)
            }
        }

        fun back() {
            fragmentPopStack()
        }
    }
}