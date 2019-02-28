package com.kotlin.viaggio.view.traveling.detail

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_detail.*
import java.io.File


class TravelingDetailFragment:BaseFragment<TravelingDetailFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_detail, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgDir = File(context?.filesDir, "images/")

        if(TextUtils.isEmpty(getViewModel().travelOfDay.themeImageName).not()){
            val imgFile = File(imgDir, getViewModel().travelOfDay.themeImageName)
            if (imgFile.exists()) {
                Uri.fromFile(imgFile).let { uri ->
                    Glide.with(travelingDetailDayImg)
                        .load(uri)
                        .into(travelingDetailDayImg)
                }
            }
        }
        getViewModel().travelOfDayImageChange.observe(this, Observer {
            it.getContentIfNotHandled()?.let {imagePath ->
                val imgFile = File(imgDir, imagePath)
                if (imgFile.exists()) {
                    Uri.fromFile(imgFile).let { uri ->
                        Glide.with(travelingDetailDayImg)
                            .load(uri)
                            .into(travelingDetailDayImg)
                    }
                }
            }
        })

        travelingDetailDayTravelCardList.layoutManager = LinearLayoutManager(context!!)
        val adapter = TravelCardAdapter()
        travelingDetailDayTravelCardList.adapter = adapter
        getViewModel().travelCardPagedLiveData.observe(this, Observer{
            getViewModel().existTravelCard.set(it.size>0)
            adapter.submitList(it)
        })
    }
    inner class ViewHandler{
        fun back(){
            fragmentPopStack()
        }
        fun add(){
            TravelingDetailActionDialogFragment().show(fragmentManager!!,TravelingDetailActionDialogFragment.TAG)
        }
        fun chooseTheme(){
            baseIntent("http://viaggio.kotlin.com/traveling/theme/")
        }

        fun travelCardCreate(){
            baseIntent("http://viaggio.kotlin.com/traveling/enroll/")
        }
    }

    inner class TravelCardAdapter: PagedListAdapter<TravelCard, TravelCardViewHolder>(object : DiffUtil.ItemCallback<TravelCard>(){
        override fun areItemsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TravelCard, newItem: TravelCard) = oldItem == newItem
    }){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = TravelCardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_card, parent, false))

        override fun onBindViewHolder(holder: TravelCardViewHolder, position: Int) {
        }
    }
    inner class TravelCardViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCardBinding>(view)
    }
}