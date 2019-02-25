package com.kotlin.viaggio.view.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.WorkerName
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.ioc.module.common.HasAndroidXFragmentInjector
import com.kotlin.viaggio.worker.TimeCheckWorker
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class BaseActivity<E : ViewModel> : AppCompatActivity(), HasAndroidXFragmentInjector {
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    internal lateinit var viewModel: E
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService

    var viewModelProvider: WeakReference<ViewModelProvider>? = null
    var loadingDialogFragment: LoadingDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        (getViewModel() as BaseViewModel).initialize()

        val traveling = prefUtilService.getBool(AndroidPrefUtilService.Key.TRAVELING).blockingGet()
        if(traveling){
            val timeCheckWork = PeriodicWorkRequestBuilder<TimeCheckWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(WorkerName.TRAVELING_OF_DAY_CHECK.name, ExistingPeriodicWorkPolicy.KEEP, timeCheckWork)
        }
    }

    override fun androidXFragmentInjector() = fragmentInjector

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

    fun baseShowLeftFragment(fragment:BaseFragment<*>){
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.layout_left_in, R.anim.layout_left_out)
            .replace(R.id.content_frame, fragment, null)
            .commit()
    }
    fun baseShowLeftAddBackFragment(fragment:BaseFragment<*>){
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(R.anim.layout_left_in, R.anim.layout_left_out,R.anim.layout_pop_left_in, R.anim.layout_pop_left_out)
            .replace(R.id.content_frame, fragment, null)
            .commit()
    }
    fun baseShowRightFragment(fragment:BaseFragment<*>){
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.layout_right_in, R.anim.layout_right_out)
            .replace(R.id.content_frame, fragment, null)
            .commit()
    }
    fun baseShowRightAddBackFragment(fragment:BaseFragment<*>){
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(R.anim.layout_right_in, R.anim.layout_right_out,R.anim.layout_pop_right_in, R.anim.layout_pop_right_out)
            .replace(R.id.content_frame, fragment, null)
            .commit()
    }
    fun baseShowTopFragment(fragment:BaseFragment<*>){
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.layout_top_in, R.anim.layout_top_out)
            .replace(R.id.content_frame, fragment, null)
            .commit()
    }
    fun baseShowTopAddBackFragment(fragment:BaseFragment<*>){
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(R.anim.layout_top_in, R.anim.layout_top_out,R.anim.layout_pop_top_in, R.anim.layout_pop_top_out)
            .replace(R.id.content_frame, fragment, null)
            .commit()
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
}