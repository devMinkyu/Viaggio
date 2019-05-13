package com.kotlin.viaggio.view.travel

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.Traveled
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.travel.kinds.TravelKindsBottomSheetDialogFragment
import com.kotlin.viaggio.view.travel.option.TravelOptionBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.android.synthetic.main.item_travel.view.*
import org.jetbrains.anko.support.v4.toast
import java.text.SimpleDateFormat
import java.util.*


class TravelFragment : BaseFragment<TravelFragmentViewModel>() {
    companion object {
        val TAG: String = TravelFragment::class.java.simpleName
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerAdapter = ArrayAdapter<String>(context!!, R.layout.spinner_continent_item, getViewModel().travelOption)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_continent_item)

        travelSpinner.adapter = spinnerAdapter
        travelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getViewModel().optionCheck(position)
            }
        }

        getViewModel().travelListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { travelList ->
                travelPager.adapter = TravelPagerAdapter(travelList)
                travelPager.offscreenPageLimit = 3
                travelPager.currentItem = if (travelList.isNotEmpty()) {
                    travelList.size - 1
                } else {
                    0
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if(getViewModel().isTravelRefresh){
            getViewModel().fetchData()
        }
    }

    override fun onStop() {
        super.onStop()
        getViewModel().isTravelRefresh = true
    }

    inner class TravelPagerAdapter(private val travelList:List<Travel>):PagerAdapter(){
        private val imageList = listOf(
            ResourcesCompat.getDrawable(resources, R.drawable.base_image1, null),
            ResourcesCompat.getDrawable(resources, R.drawable.base_image2, null),
            ResourcesCompat.getDrawable(resources, R.drawable.base_image3, null),
            ResourcesCompat.getDrawable(resources, R.drawable.base_image4, null),
            ResourcesCompat.getDrawable(resources, R.drawable.base_image5, null),
            ResourcesCompat.getDrawable(resources, R.drawable.base_image6, null),
            ResourcesCompat.getDrawable(resources, R.drawable.base_image7, null)
        )
        override fun isViewFromObject(view: View, `object`: Any) = view == `object`
        override fun getCount() = travelList.size + 1
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context).inflate(R.layout.item_travel, container, false)
            val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelBinding>(view)!!
            if (travelList.size > position) {
                val travel = Traveled()
                travelList[position].apply {
                    travel.id = id
                    travel.title = title
                    travel.period = if(endDate == null){
                        "${SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH).format(startDate)} ~"
                    } else{
                        "${SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH).format(startDate)} ~ ${SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH).format(endDate)}"
                    }
                }
                binding.data = travel

                if(TextUtils.isEmpty(travelList[position].imageName)){
                    Glide.with(view.travelBackground)
                        .load(imageList[Random().nextInt(imageList.size)])
                        .centerCrop()
                        .into(view.travelBackground)
                }else{
                    Glide.with(view.travelBackground)
                        .load(travelList[position].imageName)
                        .centerCrop()
                        .into(view.travelBackground)
                }
            } else {
                binding.data = null
            }


            view.travelNonBack.setOnClickListener {
                TravelKindsBottomSheetDialogFragment().show(fragmentManager!!, TravelKindsBottomSheetDialogFragment.TAG)
            }
            view.travelBackground.setOnClickListener {
                if (travelList.size > position) {
                    getViewModel().selectedTravelId(travelList[position].id)
                    baseIntent("http://viaggio.kotlin.com/traveling/days/")
                }
            }
            view.travelMore.setOnClickListener {
                if (travelList.size > position) {
                    getViewModel().selectedTravelId(travelList[position].id)
                    TravelOptionBottomSheetDialogFragment().show(fragmentManager!!, TravelOptionBottomSheetDialogFragment.TAG)
                }
            }
            container.addView(view)
            return view
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    inner class ViewHandler {
        fun setting() {
            baseIntent("http://viaggio.kotlin.com/home/main/setting/")
        }
    }
}