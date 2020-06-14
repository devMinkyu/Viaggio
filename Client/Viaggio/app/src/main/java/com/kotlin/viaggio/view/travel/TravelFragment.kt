package com.kotlin.viaggio.view.travel

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.Traveled
import com.kotlin.viaggio.databinding.FragmentTravelBinding
import com.kotlin.viaggio.extenstions.baseIntent
import com.kotlin.viaggio.extenstions.imageName
import com.kotlin.viaggio.extenstions.showDialog
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.travel.kinds.TravelKindsBottomSheetDialogFragment
import com.kotlin.viaggio.view.travel.option.TravelOptionBottomSheetDialogFragment
import com.kotlin.viaggio.view.traveling.TravelingFinishActionDialogFragment
import kotlinx.android.synthetic.main.fragment_travel.*
import kotlinx.android.synthetic.main.item_travel.view.*
import java.text.SimpleDateFormat
import java.util.*


class TravelFragment : BaseFragment<TravelFragmentViewModel>() {
    companion object {
        val TAG: String = TravelFragment::class.java.simpleName
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.window?.statusBarColor = ResourcesCompat.getColor(resources, R.color.white_three, null)
    }
    lateinit var binding: FragmentTravelBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerAdapter =
            ArrayAdapter(requireContext(), R.layout.spinner_continent_item, getViewModel().travelOption)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_continent_item)

        travelSpinner.adapter = spinnerAdapter
        travelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                getViewModel().optionCheck(position)
            }
        }

        getViewModel().travelListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { travelList ->
                travelPager.adapter = TravelPagerAdapter(travelList)
                travelPager.offscreenPageLimit = 3
                val position =
                    if (getViewModel().chooseNum == null) {
                        if (travelList.isNotEmpty()) {
                            travelList.size - 1
                        } else {
                            0
                        }
                    } else {
                        getViewModel().chooseNum!!
                    }
                getViewModel().chooseNum = null
                travelPager.setCurrentItem(position, false)
            }
        })

//        context?.let { mContext ->
//            MobileAds.initialize(mContext)
//            val adRequest = AdRequest.Builder().build()
//            adView.loadAd(adRequest)
//        }
    }

    override fun onStart() {
        super.onStart()
        if (getViewModel().isTravelRefresh) {
            getViewModel().fetchData()
        }
    }

    override fun onStop() {
        super.onStop()
        getViewModel().isTravelRefresh = true
    }

    inner class TravelPagerAdapter(private val travelList: List<Travel>) : PagerAdapter() {
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
                    travel.id = localId
                    travel.title = title
                    travel.period = if (endDate == null) {
                        "${SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(startDate)} ~"
                    } else {
                        if(travelKind == 2) {
                            SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(startDate)
                        } else {
                            "${SimpleDateFormat(
                                "yyyy.MM.dd",
                                Locale.getDefault()
                            ).format(startDate)} ~ ${SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(endDate)}"
                        }
                    }
                }
                binding.data = travel
                binding.traveling = if(travel.id == getViewModel().travelingId) ObservableBoolean(true) else ObservableBoolean(false)

                if (TextUtils.isEmpty(travelList[position].imageName)) {
                    Glide.with(view.travelBackground)
                        .load(imageList[Random().nextInt(imageList.size)])
                        .centerCrop()
                        .into(view.travelBackground)
                } else {
                    if(imageDir.exists()) {
                        Glide.with(view.travelBackground)
                            .load(requireContext().imageName(travelList[position].imageName))
                            .centerCrop()
                            .into(view.travelBackground)
                    }
                }
            } else {
                binding.data = null
            }


            view.travelNonBack.setOnClickListener {
                showDialog(TravelKindsBottomSheetDialogFragment(), TravelKindsBottomSheetDialogFragment.TAG)
            }
            view.travelBackground.setOnClickListener {
                if (travelList.size > position) {
                    getViewModel().selectedTravelId(travelList[position].localId)
                    if(travelList[position].travelKind == 2) {
                        baseIntent("http://viaggio.kotlin.com/traveling/day/trip/")
                    } else {
                        baseIntent("http://viaggio.kotlin.com/traveling/days/")
                    }
                }
            }
            view.travelMore.setOnClickListener {
                if (travelList.size > position) {
                    getViewModel().chooseNum = position
                    getViewModel().selectedTravelId(travelList[position].localId)
                    showDialog(TravelOptionBottomSheetDialogFragment(), TravelOptionBottomSheetDialogFragment.TAG)
                }
            }
            view.domesticsName.setOnClickListener {
                showDialog(TravelingFinishActionDialogFragment(), TravelingFinishActionDialogFragment.TAG)
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
//        private fun noticeHighlight(textView: TextView) {
//            val notice:String = resources.getString(R.string.non_travel_notice)
//            val firstIndex = notice.indexOf("%&!&%", 0)
//            val lastIndex = notice.indexOf("%&!&%", firstIndex + 1) - 5
//
//            val msg = notice.replace("%&!&%", "")
//            if(firstIndex != -1) {
//                val spannableBuilder = SpannableStringBuilder(msg)
//                context?.let { contextVal ->
//                    val font = ResourcesCompat.getFont(contextVal, Typeface.BOLD)
//                    spannableBuilder.setSpan(CustomTypefaceSpan("", font!!), firstIndex + 1, lastIndex + 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//                    textView.text = spannableBuilder
//                }
//            } else {
//                textView.text = msg
//            }
//        }
    }

    inner class ViewHandler {
        fun setting() {
            baseIntent("http://viaggio.kotlin.com/home/main/setting/")
        }
    }
}