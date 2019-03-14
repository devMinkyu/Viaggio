package com.kotlin.viaggio.view.traveling

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ShadowTransformer
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.databinding.ItemTravelingBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.common.CardAdapter
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailFragment
import com.nightonke.boommenu.BoomButtons.HamButton
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_traveling.*
import kotlinx.android.synthetic.main.item_traveling.view.*
import org.jetbrains.anko.support.v4.toast
import java.io.File


class TravelingFragment : BaseFragment<TravelingFragmentViewModel>(){
    companion object {
        val TAG:String = TravelingFragment::class.java.simpleName
    }
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(travelingContainer, SlidrConfig.Builder().position(
                SlidrPosition.LEFT).build())
    }
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/")
            }
        })

        getViewModel().travelOfDayListLiveData.observe(this, Observer { list ->
            val argbEvaluator = android.animation.ArgbEvaluator()
            val mViews = mutableListOf<CardView?>()
            for(index in 0 until list.size){
                mViews.add(null)
            }
            val adapter = TravelingOfDayPager(list, mViews)
            val mCardShadowTransformer = ShadowTransformer(travelingList, adapter)
            mCardShadowTransformer.enableScaling(true)

            travelingList.adapter = adapter
            travelingList.setPageTransformer(false, mCardShadowTransformer)
            travelingList.offscreenPageLimit = 3
            val colors = mutableListOf(
                resources.getColor(R.color.color1, null),
                resources.getColor(R.color.color2, null),
                resources.getColor(R.color.color3, null),
                resources.getColor(R.color.color4, null),
                resources.getColor(R.color.color1, null),
                resources.getColor(R.color.color2, null),
                resources.getColor(R.color.color3, null),
                resources.getColor(R.color.color4, null)
            )
            travelingList.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageSelected(position: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    getViewModel().setSelectedTravelingOfDay(list[position].id)
                    if ((position < adapter.count - 1) && position < (colors.size - 1)) {
                        travelingContainer.setBackgroundColor(
                            argbEvaluator.evaluate(
                                positionOffset,
                                colors[position],
                                colors[position + 1]
                            ) as Int
                        )
                    } else {
                        travelingContainer.setBackgroundColor(colors[colors.size - 1])
                    }
                }
            })
        })
    }

    inner class ViewHandler{

    }

    inner class TravelingOfDayPager(private val list: MutableList<TravelOfDay>, private val mViews:MutableList<CardView?>):PagerAdapter(), CardAdapter{
        private var mBaseElevation = 0f

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`
        override fun getCount() = list.size
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(context).inflate(R.layout.item_traveling, container, false)
            val binding = DataBindingUtil.bind<ItemTravelingBinding>(view)!!
            binding.data = list[position]

            val imgDir = File(context?.filesDir, "images/")
            list[position].themeImageName.let { themeImageName ->
                val imgFile = File(imgDir, themeImageName)
                if (imgFile.exists()) {
                    Uri.fromFile(imgFile).let { uri ->
                        Glide.with(view.traveledBackground)
                            .load(uri)
                            .centerCrop()
                            .into(view.traveledBackground)
                    }
                }
            }
            container.addView(view)
            ViewCompat.setTransitionName(view.traveledBackground, list[position].id.toString())

            val cardView = view.cardView
            if(mBaseElevation == 0f){
                mBaseElevation = cardView.cardElevation
            }
            cardView.maxCardElevation = mBaseElevation * CardAdapter.MAX_ELEVATION_FACTOR
            mViews[position] = cardView

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getBaseElevation() = mBaseElevation
        override fun getCardViewAt(position: Int)= mViews[position]
    }
}