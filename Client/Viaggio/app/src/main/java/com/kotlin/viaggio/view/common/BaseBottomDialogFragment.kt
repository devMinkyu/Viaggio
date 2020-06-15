package com.kotlin.viaggio.view.common

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class BaseBottomDialogFragment<E : ViewModel> : AbstractBaseBottomDialogFragment(),
    HasAndroidInjector {
    @Inject
    internal lateinit var viewModel: E
    @Inject
    lateinit var frameworkActivityInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return frameworkActivityInjector
    }

    var viewModelProvider: WeakReference<ViewModelProvider>? = null
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (getViewModel() as BaseViewModel).initialize()
    }

    override fun onStop() {
        super.onStop()
        view?.let {
            hideKeyBoard(it)
        }
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
}