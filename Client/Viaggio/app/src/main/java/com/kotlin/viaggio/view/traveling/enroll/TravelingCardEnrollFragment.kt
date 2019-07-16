package com.kotlin.viaggio.view.traveling.enroll

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentTravelingCardEnrollBinding
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.traveling.option.TravelingCitiesActionDialogFragment
import com.kotlin.viaggio.view.traveling.option.TravelingDayCountActionDialogFragment
import com.kotlin.viaggio.view.traveling.option.TravelingThemesActionDialogFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_traveling_card_enroll.*
import kotlinx.android.synthetic.main.item_travel_card_theme.view.*
import kotlinx.android.synthetic.main.item_traveling_card_img.view.*
import org.jetbrains.anko.design.snackbar
import java.text.SimpleDateFormat
import java.util.*


class TravelingCardEnrollFragment : BaseFragment<TravelingCardEnrollFragmentViewModel>() {
    lateinit var binding: FragmentTravelingCardEnrollBinding
    lateinit var adapter: RecyclerView.Adapter<TravelCardEnrollViewHolder>
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(enroll_container, SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .build())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_card_enroll, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
            }
        })

        travelCardEnrollImageList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        getViewModel().imageLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                adapter = object : RecyclerView.Adapter<TravelCardEnrollViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelCardEnrollViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_card_img, parent, false))
                    override fun getItemCount() = getViewModel().imageList.size + 1
                    override fun onBindViewHolder(holder: TravelCardEnrollViewHolder, position: Int) {
                        holder.binding?.viewHandler = holder.TravelCardEnrollViewHandler()
                        if(position > 0){
                            holder.itemView.travelingPagerImg.visibility = View.VISIBLE
                            holder.loadImage(getViewModel().imageList[position - 1])
                        }else{
                            holder.itemView.travelingPagerImg.visibility = View.GONE
                        }
                    }
                }

                travelCardEnrollImageList.adapter = adapter
            }
        })

        travelCardEnrollImageList.setOnScrollChangeListener { _, _, _, _, _ ->
            travelCardEnrollImageList?.let {
                if (travelCardEnrollImageList.canScrollHorizontally(-1).not()) {
                    enableSliding(true)
                } else {
                    enableSliding(false)
                }
            }
        }
        loadView()

        getViewModel().themeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                loadView()
            }
        })
    }

    private fun loadView(){
        val main = travelingEnrollThemeList as ViewGroup

        if (main.childCount > 0) {
            main.removeViews(2, main.childCount - 2)
        }
        if(getViewModel().themeList.isNullOrEmpty()){
            val inflater = layoutInflater
            val themeListView = inflater.inflate(R.layout.item_travel_card_theme, null)
            themeListView.themeName.text = resources.getString(R.string.theme_start)
            themeListView.themeName.setOnClickListener {
                TravelingThemesActionDialogFragment().show(fragmentManager!!, TravelingThemesActionDialogFragment.TAG)
            }
            main.addView(themeListView)
        }else{
            getViewModel().themeList.map {
                val inflater = layoutInflater
                val themeListView = inflater.inflate(R.layout.item_travel_card_theme, null)
                themeListView.themeName.text = it
                themeListView.themeName.setOnClickListener {
                    TravelingThemesActionDialogFragment().show(fragmentManager!!, TravelingThemesActionDialogFragment.TAG)
                }
                themeListView.right = resources.getDimension(R.dimen.theme_traveling_card).toInt()
                main.addView(themeListView)
            }
        }
    }

    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }
        fun save() {
            showLoading()
            getViewModel().saveCard()
        }

        fun enrollOfTime() {
            val cal = Calendar.getInstance()
            getViewModel().contents.set("${getViewModel().contents.get()}\n${SimpleDateFormat(resources.getString(R.string.travel_of_day_time_pattern), Locale.ENGLISH).format(cal.time)}\n")

        }

        fun changeCountry(){
            getViewModel().selectedCountry()
            TravelingCitiesActionDialogFragment().show(fragmentManager!!, TravelingCitiesActionDialogFragment.TAG)
        }

        fun changeDayCount(){
            TravelingDayCountActionDialogFragment().show(fragmentManager!!, TravelingDayCountActionDialogFragment.TAG)
        }
    }

    inner class TravelCardEnrollViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCardImgBinding>(view)

        private var selectedImage:Bitmap? = null
        fun loadImage(image:Any){
            when (image) {
                is Bitmap -> {
                    selectedImage = image
                    Glide.with(context!!)
                        .load(image)
                        .into(itemView.travelingPagerImg)
                }
                is String -> {
                    Glide.with(itemView)
                        .load(image)
                        .into(itemView.travelingPagerImg)
                }
                else -> { }
            }
        }
        inner class TravelCardEnrollViewHandler{
            fun imageAdd(){
                view?.let {
                    hideKeyBoard(it)
                }
                getViewModel().permissionCheck(
                    rxPermission.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ).observe(this@TravelingCardEnrollFragment, Observer {
                        if(it) {
                            baseIntent("http://viaggio.kotlin.com/traveling/enroll/image/")
                        } else {
                            view?.snackbar(resources.getString(R.string.storage_permission))
                        }
                    })
            }
        }
    }
}