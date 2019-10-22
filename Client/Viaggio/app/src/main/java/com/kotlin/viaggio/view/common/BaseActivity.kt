package com.kotlin.viaggio.view.common

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import net.skoumal.fragmentback.BackFragmentHelper
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class BaseActivity<E : ViewModel> : AppCompatActivity(), HasSupportFragmentInjector{
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    internal lateinit var viewModel: E
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService

    var viewModelProvider: WeakReference<ViewModelProvider>? = null
    private var loadingDialogFragment: LoadingDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        (getViewModel() as BaseViewModel).initialize()
    }

    override fun supportFragmentInjector() = fragmentInjector
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

    fun showLoading() {
        val loadingDialogFragment1Val = loadingDialogFragment?.run {
            return
        }?:supportFragmentManager.findFragmentByTag(LoadingDialogFragment.TAG)
        val loadingDialogFragmentVal = loadingDialogFragment1Val?.run {
            return
        }?:LoadingDialogFragment()
        loadingDialogFragment = loadingDialogFragmentVal
        loadingDialogFragmentVal.show(supportFragmentManager, LoadingDialogFragment.TAG)
    }

    fun stopLoading() {
        loadingDialogFragment = null
        val loadingDialogFragment1Val = supportFragmentManager.findFragmentByTag(LoadingDialogFragment.TAG)
        loadingDialogFragment1Val?.let {
            (it as LoadingDialogFragment).dismiss()
        }
    }
    fun showKeyBoard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }
    fun hideKeyBoard(view: View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun handleCustomOnBackPressed(): Boolean {
        return false
    }
    override fun onBackPressed() {
        if (!BackFragmentHelper.fireOnBackPressedEvent(this)) {
            // lets do the default back action if fragments don't consume it
            if (!handleCustomOnBackPressed()) {
                super.onBackPressed()
            }
        }
    }
}