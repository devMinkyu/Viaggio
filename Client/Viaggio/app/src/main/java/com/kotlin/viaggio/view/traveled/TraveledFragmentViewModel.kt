package com.kotlin.viaggio.view.traveled

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.model.TravelModel
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TraveledFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var travelModel: TravelModel

    lateinit var traveledPagedLiveData: LiveData<PagedList<Travel>>

    val existTraveled:ObservableBoolean = ObservableBoolean(false)
    override fun initialize() {
        super.initialize()
        val factory: DataSource.Factory<Int, Travel>
                = travelModel.getTravels()
        val pagedListBuilder: LivePagedListBuilder<Int, Travel> = LivePagedListBuilder<Int, Travel>(factory,
            20)
        traveledPagedLiveData = pagedListBuilder.build()

        val disposable = rxEventBus.travelFinish.
            subscribe {
                if(it){
                    traveledPagedLiveData.value?.dataSource?.invalidate()
                }
            }
        addDisposable(disposable)
    }
}
