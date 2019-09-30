package com.kotlin.viaggio.view.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.viaggio.BuildConfig
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import net.skoumal.fragmentback.BackFragment
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class BaseDialogFragment<E : ViewModel> : AbstractBaseDialogFragment(), HasSupportFragmentInjector, BackFragment {
    @Inject
    internal lateinit var viewModel: E
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    var viewModelProvider: WeakReference<ViewModelProvider>? = null
    override fun supportFragmentInjector() = fragmentInjector
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (getViewModel() as BaseViewModel).initialize()
    }

    fun getViewModel(): E =
        viewModelProvider.let { vmpRef ->
            vmpRef?.get()?.let {
                it
            } ?: getNewViewModelProvider()
        }.get(viewModel::class.java)

    private fun getNewViewModelProvider(): ViewModelProvider {
        val nonNullViewModelProviderVal = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                try {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        })
        viewModelProvider = WeakReference(nonNullViewModelProviderVal)
        return nonNullViewModelProviderVal
    }

    fun baseIntent(uri:String){
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(uri)
        )
        intent.setPackage(BuildConfig.APPLICATION_ID)
        startActivity(intent)
    }

    fun showLoading() {
        activity?.let {
            (it as BaseActivity<*>).showLoading()
        }
    }
    fun stopLoading() {
        activity?.let {
            (it as BaseActivity<*>).stopLoading()
        }
    }

    fun showKeyBoard(view: View) {
        activity?.let {
            (it as BaseActivity<*>).showKeyBoard(view)
        }
    }

    fun hideKeyBoard(view: View) {
        activity?.let {
            (it as BaseActivity<*>).hideKeyBoard(view)
        }
    }

    // back interface
    override fun onBackPressed(): Boolean {
        return false
    }
    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }
}