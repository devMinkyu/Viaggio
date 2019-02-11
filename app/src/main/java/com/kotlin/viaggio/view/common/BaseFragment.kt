package com.kotlin.viaggio.view.common

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kotlin.viaggio.ioc.module.common.AndroidXInjection
import com.kotlin.viaggio.ioc.module.common.HasAndroidXFragmentInjector
import dagger.android.DispatchingAndroidInjector
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class BaseFragment<E:ViewModel>:Fragment(), HasAndroidXFragmentInjector {

    @Inject
    internal lateinit var viewModel: E
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    var viewModelProvider: WeakReference<ViewModelProvider>? = null

    override fun androidXFragmentInjector() = fragmentInjector

    override fun onAttach(context: Context?) {
        AndroidXInjection.inject(this)
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
        val nonNullViewModelProviderVal =  ViewModelProvider(viewModelStore, object: ViewModelProvider.Factory {
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

}