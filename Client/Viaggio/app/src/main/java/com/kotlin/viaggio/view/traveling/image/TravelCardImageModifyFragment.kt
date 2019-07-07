package com.kotlin.viaggio.view.traveling.image

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentTravelCardImageModifyImageBinding
import com.kotlin.viaggio.databinding.ItemTravelingCardModifyImgBinding
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_travel_card_image_modify_image.*
import kotlinx.android.synthetic.main.item_traveling_card_modify_img.view.*
import org.jetbrains.anko.design.snackbar
import java.io.File


class TravelCardImageModifyFragment : BaseFragment<TravelCardImageModifyFragmentViewModel>() {
    lateinit var binding: FragmentTravelCardImageModifyImageBinding
    lateinit var imageAdapter: RecyclerView.Adapter<TravelCardCurrentImageViewHolder>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_card_image_modify_image, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        travelCardCurrentImageList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        travelCardCurrentImageList.addItemDecoration(TravelCardImageItemDecoration())
        travelCardNewImageList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        travelCardNewImageList.addItemDecoration(TravelCardImageItemDecoration())

        getViewModel().imageNamesListLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                val imgDir = File(context?.filesDir, "images/")
                if(imgDir.exists()) {
                    imageAdapter = object :RecyclerView.Adapter<TravelCardCurrentImageViewHolder>() {
                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                                = TravelCardCurrentImageViewHolder(layoutInflater.inflate(R.layout.item_traveling_card_modify_img, parent, false))
                        override fun getItemCount() = list.size
                        override fun onBindViewHolder(holder: TravelCardCurrentImageViewHolder, position: Int) {
                            holder.imageBinding(list[position])
                            holder.binding?.viewHandler = holder.TravelCardCurrentImageViewHandler()
                        }
                    }
                    travelCardCurrentImageList.adapter = imageAdapter
                }
            }
        })

        getViewModel().imageLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { list ->
                travelCardNewImageList.adapter = object :RecyclerView.Adapter<TravelCardNewImageViewHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelCardNewImageViewHolder(layoutInflater.inflate(R.layout.item_traveling_card_modify_img, parent, false))
                    override fun getItemCount() = list.size + 1
                    override fun onBindViewHolder(holder: TravelCardNewImageViewHolder, position: Int) {
                        holder.binding?.viewHandler = holder.TravelCardNewImageViewHandler()
                        if(position > 0){
                            holder.itemView.travelingPagerImg.visibility = View.VISIBLE
                            holder.itemView.travelingPagerImgDelete.visibility = View.GONE
                            holder.loadImage(getViewModel().imageList[position - 1])
                        }else{
                            holder.itemView.travelingPagerImg.visibility = View.GONE
                            holder.itemView.travelingPagerImgDelete.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    inner class ViewHandler {
        fun back() {
            fragmentPopStack()
        }
        fun confirm() {
            showLoading()
            getViewModel().save().observe(this@TravelCardImageModifyFragment, Observer {
                if(it) {
                    stopLoading()
                    fragmentPopStack()
                } else {
                    stopLoading()
                    view?.snackbar(resources.getString(R.string.travel_card_image_modify_fail))
                }
            })
        }
    }

    inner class TravelCardCurrentImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelingCardModifyImgBinding>(view)
        private lateinit var fileNamePath: String

        fun imageBinding(string: String) {
            fileNamePath = string
            Glide.with(itemView.travelingPagerImg)
                .load(string)
                .into(itemView.travelingPagerImg)

        }

        inner class TravelCardCurrentImageViewHandler: TravelCardModifyImageViewHandler {
            override fun delete() {
                getViewModel().deleteImage(fileNamePath).observe(this@TravelCardImageModifyFragment, Observer {
                    imageAdapter.notifyItemRemoved(it)
                })
            }
            override fun add() {}
        }
    }
    inner class TravelCardNewImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ItemTravelingCardModifyImgBinding>(view)

        fun loadImage(image:Any){
            when (image) {
                is Bitmap -> {
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

        inner class TravelCardNewImageViewHandler: TravelCardModifyImageViewHandler {
            override fun delete() {}
            override fun add() {
                getViewModel().permissionCheck(
                    rxPermission.request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ).observe(this@TravelCardImageModifyFragment, Observer {
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


interface TravelCardModifyImageViewHandler{
    fun delete()
    fun add()
}

class TravelCardImageItemDecoration :
    RecyclerView.ItemDecoration() {
    private var firstHorMargin: Float? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if(parent.getChildAdapterPosition(view) == 0) {
            val firstHorMarginVal1 = firstHorMargin
                ?: (parent.context.resources.getDimension(R.dimen.common_margin))
            outRect.left = firstHorMarginVal1.toInt()
        }
    }
}
