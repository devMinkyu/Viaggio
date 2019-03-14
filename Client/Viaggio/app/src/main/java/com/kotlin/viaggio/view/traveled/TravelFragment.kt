package com.kotlin.viaggio.view.traveled

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.android.synthetic.main.item_travel.view.*
import java.io.File


class TravelFragment:BaseFragment<TravelFragmentViewModel>() {
    companion object {
        val TAG:String = TravelFragment::class.java.simpleName
    }
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel().travelListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {travelList ->
                travelPager.adapter = object :PagerAdapter(){
                    override fun isViewFromObject(view: View, `object`: Any) = view == `object`
                    override fun getCount() = travelList.size + 1
                    override fun instantiateItem(container: ViewGroup, position: Int): Any {
                        val view = LayoutInflater.from(context).inflate(R.layout.item_travel, container, false)
                        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelBinding>(view)!!
                        if(travelList.size > position){
                            binding.data = travelList[position]
                            val imgDir = File(context?.filesDir, "images/")
                            travelList[position].themeImageName.let {themeImageName->
                                val imgFile = File(imgDir, themeImageName)
                                if (imgFile.exists()) {
                                    Uri.fromFile(imgFile).let { uri ->
                                        Glide.with(view.travelBackground)
                                            .load(uri)
                                            .centerCrop()
                                            .into(view.travelBackground)
                                    }
                                }
                            }
                        }else{
                            binding.data = null
                        }
                        view.travelAdd.setOnClickListener {

                        }

                        view.travelBackground.setOnClickListener {viewVal ->
                            if(travelList.size > position){

                            }
                        }

                        container.addView(view)
                        return view
                    }

                    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                        container.removeView(`object` as View)
                    }

                }
                travelPager.currentItem = if(travelList.isNotEmpty()){
                    travelList.size - 1
                }else{
                    0
                }
            }
        })
        travelPager.offscreenPageLimit = 3
    }

    inner class ViewHandler
}