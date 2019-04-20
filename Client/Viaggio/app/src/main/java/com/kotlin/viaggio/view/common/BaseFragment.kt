package com.kotlin.viaggio.view.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.BuildConfig
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.ioc.module.common.AndroidXInjection
import com.kotlin.viaggio.ioc.module.common.HasAndroidXFragmentInjector
import com.kotlin.viaggio.worker.TimeCheckWorker
import com.r0adkll.slidr.model.SlidrInterface
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.DispatchingAndroidInjector
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class BaseFragment<E : ViewModel> : Fragment(), HasAndroidXFragmentInjector {
    @Inject
    internal lateinit var viewModel: E
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService

    lateinit var rxPermission: RxPermissions
    var viewModelProvider: WeakReference<ViewModelProvider>? = null
    var sliderInterface: SlidrInterface? = null

    var width:Int = 0

    override fun androidXFragmentInjector() = fragmentInjector

    override fun onAttach(context: Context) {
        AndroidXInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rxPermission = RxPermissions(this)
        (getViewModel() as BaseViewModel).initialize()
        width = context!!.resources.displayMetrics.widthPixels
    }

    override fun onStart() {
        super.onStart()
        val traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
        if (traveling) {
            val timeCheckWorker = OneTimeWorkRequestBuilder<TimeCheckWorker>().build()
            WorkManager.getInstance().enqueue(timeCheckWorker)
        }
    }
    override fun onStop() {
        super.onStop()
        sliderInterface = null
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

    fun enableSliding(enable: Boolean) {
        if (enable)
            sliderInterface?.unlock()
        else
            sliderInterface?.lock()
    }

    fun baseIntent(uri:String){
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(uri)
        )
        intent.setPackage(BuildConfig.APPLICATION_ID)
        startActivity(intent)
    }

    fun fragmentPopStack(){
        fragmentManager?.popBackStack()
    }

    fun checkInternet():Boolean{
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null
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
}