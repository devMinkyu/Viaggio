package com.kotlin.viaggio.view.traveling.detail

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
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
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.event.OnSwipeTouchListener
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_detail.*
import kotlinx.android.synthetic.main.item_traveling_card.view.*
import java.io.File
import java.util.*


class TravelingDetailFragment:BaseFragment<TravelingDetailFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fade = Fade()
        fade.duration = 1000
        enterTransition = fade
//        postponeEnterTransition()
//        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_detail, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgDir = File(context?.filesDir, "images/")

        travelingDetailDayTravelCardList.animate().alpha(0f).setDuration(0).start()
        travelingDetailDayTravelCardList.animate().setDuration(1000).alpha(1f).start()

        travelingDetailDayImg.transitionName = arguments?.getString(ArgName.EXTRA_TRANSITION_NAME.name)?:""

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

        container.setOnTouchListener(object :OnSwipeTouchListener(context!!){
            override fun onSwipeBottom() {
                super.onSwipeBottom()
                fragmentPopStack()
            }
        })


//        travelingDetailDayTravelCardList.setOnTouchListener(object :OnSwipeTouchListener(context!!){
//            override fun onSwipeBottom() {
//                super.onSwipeBottom()
//                val animator: ViewPropertyAnimator = travelingDetailDayTravelCardCreate.animate().setDuration(200)
//                    .alpha(1f)
//                    .setInterpolator(AccelerateInterpolator())
//                animator.start()
//            }
//
//            override fun onSwipeTop() {
//                super.onSwipeTop()
//                val animator: ViewPropertyAnimator = travelingDetailDayTravelCardCreate.animate().setDuration(200)
//                    .alpha(0f)
//                    .setInterpolator(AccelerateInterpolator())
//                animator.start()
//            }
//        })
    }
    inner class ViewHandler{
        fun back(){
            fragmentPopStack()
        }
        fun add(){
            TravelingDetailActionDialogFragment().show(fragmentManager!!,TravelingDetailActionDialogFragment.TAG)
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

        @SuppressLint("SimpleDateFormat")
        override fun onBindViewHolder(holder: TravelCardViewHolder, position: Int) {
            holder.binding?.data = getItem(position)?.contents
            val now = Calendar.getInstance()
            now.time = getItem(position)?.enrollOfTime
            val isAMorPM = now.get(Calendar.AM_PM)
            when(isAMorPM){
                Calendar.AM -> {
                    holder.binding?.date = "${now.get(Calendar.HOUR)}:${now.get(Calendar.MINUTE)} am"
                }
                Calendar.PM -> {
                    holder.binding?.date = "${now.get(Calendar.HOUR)}:${now.get(Calendar.MINUTE)} pm"
                }
            }
            holder.loadImage(getItem(position)?.imageNames)
        }
    }
    inner class TravelCardViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingCardBinding>(view)

        fun loadImage(imageName:ArrayList<String>?){
            imageName?.let {
                val imgDir = File(context?.filesDir, "images/")
                loop@ for((i,s) in imageName.withIndex()){
                    val imgFile = File(imgDir, s)
                    when(i){
                        0 ->{
                            val params = itemView.travelCardEnrollImg1.layoutParams
                            params.width = width/2
                            itemView.travelCardEnrollImg1.layoutParams = params
                            if (imgFile.exists()) {
                                Uri.fromFile(imgFile).let { uri ->
                                    Glide.with(itemView.travelCardEnrollImg1)
                                        .load(uri)
                                        .into(itemView.travelCardEnrollImg1)
                                }
                            }
                        }
                        1->{
                            itemView.travelCardEnrollImg2.visibility = View.VISIBLE
                            val params = itemView.travelCardEnrollImg2.layoutParams
                            params.width = width/2
                            itemView.travelCardEnrollImg2.layoutParams = params
                            if (imgFile.exists()) {
                                Uri.fromFile(imgFile).let { uri ->
                                    Glide.with(itemView.travelCardEnrollImg2)
                                        .load(uri)
                                        .into(itemView.travelCardEnrollImg2)
                                }
                            }
                        }
                        2->{
                            itemView.travelCardEnrollImg3.visibility = View.VISIBLE
                            val params = itemView.travelCardEnrollImg3.layoutParams
                            params.width = width/2
                            itemView.travelCardEnrollImg3.layoutParams = params
                            if (imgFile.exists()) {
                                Uri.fromFile(imgFile).let { uri ->
                                    Glide.with(itemView.travelCardEnrollImg3)
                                        .load(uri)
                                        .into(itemView.travelCardEnrollImg3)
                                }
                            }
                        }
                        3->{
                            itemView.travelCardEnrollImg4Container.visibility = View.VISIBLE
                            val params = itemView.travelCardEnrollImg4.layoutParams
                            params.width = width/2
                            itemView.travelCardEnrollImg4.layoutParams = params
                            if (imgFile.exists()) {
                                Uri.fromFile(imgFile).let { uri ->
                                    Glide.with(itemView.travelCardEnrollImg4)
                                        .load(uri)
                                        .into(itemView.travelCardEnrollImg4)
                                }
                            }
                        }
                        4->{
                            itemView.travelCardAdditionalBackground.visibility = View.VISIBLE
                            itemView.travelCardAdditionalCount.visibility = View.VISIBLE
                            itemView.travelCardAdditionalCount.text = String.format(resources.getString(R.string.over_image_count), imageName.size - 4)
                            break@loop
                        }
                    }
                }

            }
        }
    }
}