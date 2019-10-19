package com.kotlin.viaggio.view.sign

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.SignError
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.support.v4.toast
import timber.log.Timber

class SignInFragment : BaseFragment<SignInFragmentViewModel>() {
    companion object {
        val TAG: String = SignInFragment::class.java.simpleName
        const val GOOGLE_SIGN_CODE = 200
    }
    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        if (sliderInterface == null)
            sliderInterface = Slidr.replace(
                sign_container, SlidrConfig.Builder()
                    .position(SlidrPosition.LEFT)
                    .build()
            )
    }

    override fun onStop() {
        super.onStop()
        activity?.window?.statusBarColor = ResourcesCompat.getColor(resources, R.color.white_three, null)
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
                signInEmailEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red,null))
                signInPasswordEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red,null))
                when (signError) {
                    SignError.EMAIL_NOT_FOUND -> {
                        getViewModel().email.set("")
                        signInEmailEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red,null))
                        signInEmailEdit.error = getString(R.string.err_email_not_found)
                    }
                    SignError.WRONG_PW -> {
                        getViewModel().password.set("")
                        signInPasswordEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red,null))
                        signInPasswordEdit.error = getString(R.string.err_wrong_pw)
                    }
                    SignError.DELETE_ID -> {
                        getViewModel().email.set("")
                        signInEmailEdit.setErrorTextColor(ResourcesCompat.getColorStateList(resources, R.color.light_red,null))
                        signInEmailEdit.error = getString(R.string.err_delete_id)
                    }
                    else -> {}
                }
            }
        })
        getViewModel().googleErrorMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { string ->
                view.snackbar(string)
            }
        })
    }

    inner class ViewHandler {
        fun signIn() {
            if (checkInternet()) {
                getViewModel().validateSignIn()
                showLoading()
            } else {
                showNetWorkError()
            }
        }
        fun googleSingIn() {
            showLoading()
            val signInIntent = getViewModel().googleSignInClient.signInIntent
            startActivityForResult(signInIntent, GOOGLE_SIGN_CODE)
        }
        fun signUp() {
            baseIntent("http://viaggio.kotlin.com/login/create/")
        }
        fun back() {
            fragmentPopStack()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnCompleteListener {
                if(it.isSuccessful) {
                    getViewModel().handleSignInResult(task.result)
                } else {
                    val e = it.exception
                    Timber.tag(TAG).d(e)
                    throw e as Throwable
                }
            }
        }
    }
}