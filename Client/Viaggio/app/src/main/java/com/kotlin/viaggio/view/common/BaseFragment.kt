package com.kotlin.viaggio.view.common

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.LocalDataSource
import com.kotlin.viaggio.extenstions.showDialog
import com.kotlin.viaggio.view.travel.TravelFragmentViewModel
import com.kotlin.viaggio.worker.TimeCheckWorker
import com.r0adkll.slidr.model.SlidrInterface
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import net.skoumal.fragmentback.BackFragment
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

abstract class BaseFragment<E : ViewModel> : Fragment(), HasSupportFragmentInjector, BackFragment {
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
    val imageDir by lazy {
        return@lazy File(context!!.filesDir, LocalDataSource.IMG_FOLDER)
    }
    override fun supportFragmentInjector() = fragmentInjector
    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
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
            WorkManager.getInstance(context!!).enqueue(timeCheckWorker)
        }
    }
    override fun onStop() {
        super.onStop()
        sliderInterface = null
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
//        ViewModelProvider(this, SavedStateVMFactory(this))
//        val nonNullViewModelProviderVal = ViewModelProvider(viewModelStore, SavedStateVMFactory(this))
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

    fun fragmentPopStack(){
        parentFragmentManager.popBackStack()
    }

    fun checkInternet():Boolean {
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
    fun showNetWorkError(){
        showDialog(NetworkDialogFragment(), NetworkDialogFragment.TAG)
    }


    // back interface
    override fun onBackPressed(): Boolean {
        return false
    }
    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }
}