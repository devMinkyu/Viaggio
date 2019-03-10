package com.kotlin.viaggio.view.traveling

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.android.ShadowTransformer
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.`object`.TravelingError
import com.kotlin.viaggio.databinding.ItemTravelingBinding
import com.kotlin.viaggio.event.OnSwipeTouchListener
import com.kotlin.viaggio.view.common.BaseFragment
import com.kotlin.viaggio.view.common.CardAdapter
import com.kotlin.viaggio.view.traveling.detail.TravelingDetailFragment
import com.nightonke.boommenu.BoomButtons.HamButton
import kotlinx.android.synthetic.main.fragment_traveling.*
import kotlinx.android.synthetic.main.item_traveling.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class TravelingFragment : BaseFragment<TravelingFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        binding.travelingThemes.layoutManager = layoutManager
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModel().goToCamera.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                baseIntent("http://viaggio.kotlin.com/home/main/camera/")
            }

        })
        getViewModel().permissionRequestMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { permissionError ->
                when (permissionError) {
                    PermissionError.NECESSARY_PERMISSION -> toast(resources.getString(R.string.camera_permission))
                    else -> {
                    }
                }
            }
        })
        getViewModel().errorMsg.observe(this, Observer {
            it.getContentIfNotHandled()?.let { error ->
                when(error){
                    TravelingError.THEME_EMPTY -> toast(resources.getString(R.string.theme_empty))
                    TravelingError.COUNTRY_EMPTY -> toast(resources.getString(R.string.country_empty))
                    else -> {}
                }
            }
        })

        getViewModel().travelThemeListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { list ->
                travelingThemes.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                            = ThemeTravelingSelectedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_selected_theme, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as ThemeTravelingSelectedViewHolder
                        holder.binding?.data = list[position]
                        holder.binding?.viewHandler = ViewHandler()
                    }
                }
            }
        })

        ///
        for(index in 0 until bmb.piecePlaceEnum.pieceNumber()) {
            val builder = HamButton.Builder()
                .normalImageRes(R.drawable.ic_add_black_24dp)
                .normalTextRes(R.string.err_delete_id)
                .subNormalTextRes(R.string.necessary_permission)
                .shadowEffect(true)
                .listener {
                    toast("zzz")
                }

            bmb.addBuilder(builder)
        }

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
    fun anim() {
        getViewModel().click()
        if (getViewModel().isFabOpen) {
            travelingChangeCountry.apply {
                startAnimation(AnimationUtils.loadAnimation(context!!, R.anim.fab_close))
                isClickable = false
            }
            travelingFinishTravel.apply {
                startAnimation(AnimationUtils.loadAnimation(context!!, R.anim.fab_close))
                isClickable = false
            }
            getViewModel().isFabOpen = false
        } else {
            travelingChangeCountry.apply {
                startAnimation(AnimationUtils.loadAnimation(context!!, R.anim.fab_open))
                isClickable = true
            }
            travelingFinishTravel.apply {
                startAnimation(AnimationUtils.loadAnimation(context!!, R.anim.fab_open))
                isClickable = true
            }
            getViewModel().isFabOpen = true
        }
    }

    inner class ViewHandler{
        fun cameraOpen(){
            getViewModel().permissionCheck(
                rxPermission.request(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
        fun addTheme(){
            baseIntent("http://viaggio.kotlin.com/home/main/theme/")
        }
        @SuppressLint("SimpleDateFormat")
        fun changeDate(){
            alert {
                lateinit var datePicker: DatePicker
                customView {
                    verticalLayout {
                        datePicker = datePicker {
                            this.maxDate = System.currentTimeMillis()
                        }
                    }
                    okButton {
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.YEAR, datePicker.year)
                        cal.set(Calendar.MONTH, datePicker.month)
                        cal.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
                        getViewModel().changeStartOfDay(SimpleDateFormat(resources.getString(R.string.date_format)).format(cal.time))
                    }
                    cancelButton {
                        it.dismiss()
                    }
                }
            }.show()
        }
        fun travelStart(){
            if(getViewModel().travelStart()){
                showLoading()
            }
        }
        fun changeCountry(){
            anim()
            baseIntent("http://viaggio.kotlin.com/traveling/country/")
        }
        fun finishTravel(){
            anim()
            TravelingFinishActionDialogFragment().show(fragmentManager!!, TravelingFinishActionDialogFragment.TAG)
        }
        fun view(){
            anim()
        }

        fun traveled(){
            baseIntent("http://viaggio.kotlin.com/home/main/traveled/")
        }
        fun setting(){
            baseIntent("http://viaggio.kotlin.com/home/main/setting/")
        }
    }

    inner class ThemeTravelingSelectedViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingSelectedThemeBinding>(view)
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

            view.setOnTouchListener(object : OnSwipeTouchListener(context!!) {
                override fun onSwipeTop() {
                    super.onSwipeTop()
                    val id = binding.data?.id ?: 0
                    getViewModel().setSelectedTravelingOfDay(id)
                    val frag = TravelingDetailFragment()
                    val bundle = Bundle()
                    bundle.putString(
                        ArgName.EXTRA_TRANSITION_NAME.name,
                        ViewCompat.getTransitionName(view.traveledBackground)
                    )
                    frag.arguments = bundle
                    fragmentManager!!
                        .beginTransaction()
                        .addSharedElement(
                            view.traveledBackground,
                            ViewCompat.getTransitionName(view.traveledBackground)!!
                        )
                        .addToBackStack(null)
                        .replace(R.id.content_frame, frag)
                        .commit()
                }
            })

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