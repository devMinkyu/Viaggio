package com.kotlin.viaggio.view.travel.option

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.viaggio.R
import com.kotlin.viaggio.databinding.FragmentTravelingInstagramShareBinding
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_representative_image.*
import kotlinx.android.synthetic.main.item_traveling_representative_image.view.*
import java.io.File


class TravelingInstagramShareFragment : BaseFragment<TravelingInstagramShareFragmentViewModel>() {
    lateinit var binding: FragmentTravelingInstagramShareBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_instagram_share, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        travelingRepresentativeImageList.layoutManager = GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
        getViewModel().imageNamesListLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let { imageNames ->
                val imgDir = File(context?.filesDir, "images/")
                if (imgDir.exists()) {
                    if (imageNames.isNotEmpty()) {
                        travelingRepresentativeImage.layoutParams.also { parms ->
                            parms.width = width
                            parms.height = width
                        }
                        Glide.with(travelingRepresentativeImage)
                            .load(imageNames[0])
                            .into(travelingRepresentativeImage)
                        getViewModel().choose[0].set(true)
                        getViewModel().chooseIndex = 0

                        travelingRepresentativeImageList.adapter = object :RecyclerView.Adapter<TravelingInstagramShareViewHolder>() {
                            override fun onCreateViewHolder(
                                parent: ViewGroup,
                                viewType: Int
                            ) = TravelingInstagramShareViewHolder(layoutInflater.inflate(R.layout.item_traveling_representative_image, parent, false))
                            override fun getItemCount() = imageNames.size
                            override fun onBindViewHolder(holder: TravelingInstagramShareViewHolder, position: Int) {
                                holder.binding?.viewHandler = holder.TravelingInstagramShareViewHandler()
                                holder.binding?.choose = getViewModel().choose[position]
                                holder.imageBinding(imageNames[position])
                            }
                        }
                    }
                }
            }
        })

        getViewModel().completeLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
            }
        })
    }

    inner class ViewHandler {
        fun feed() {
            val type = "image/*"
            val mediaPath = getViewModel().list[getViewModel().chooseIndex]
            val share = Intent(Intent.ACTION_SEND)
            share.type = type
            val media = File(mediaPath)
            val uri = Uri.fromFile(media)
            share.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(share, "Share to"))
        }
        fun story() {
            val mediaPath = getViewModel().list[getViewModel().chooseIndex]
            val media = File(mediaPath)
            val stickerAssetUri:Uri = Uri.fromFile(media)
            val attributionLinkUrl = "https://www.my-aweseome-app.com/p/BhzbIOUBval/"
            val intent = Intent("com.instagram.share.ADD_TO_STORY")
//            intent.type = MEDIA_TYPE_JPEG
            intent.putExtra("interactive_asset_uri", stickerAssetUri)
            intent.putExtra("content_url", attributionLinkUrl)

            val activity = activity?.let {
                it.grantUriPermission("com.instagram.android",stickerAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if(it.packageManager.resolveActivity(intent, 0) != null) {
                    it.startActivityForResult(intent, 0)
                }
            }
        }
        fun back() {
            fragmentPopStack()
        }
        fun next() {
            getViewModel().share.set(true)
        }
    }

    override fun onBackPressed(): Boolean {
        return if(getViewModel().share.get()) {
            getViewModel().share.set(false)
            true
        } else {
            false
        }
    }

    inner class TravelingInstagramShareViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingRepresentativeImageBinding>(view)
        private lateinit var fileNamePath: String

        fun imageBinding(string: String) {
            fileNamePath = string
            val layoutParams = itemView.travelingRepresentativeContainer.layoutParams
            layoutParams.width = width / 4
            layoutParams.height = width / 4
            itemView.travelingRepresentativeContainer.layoutParams = layoutParams

            Glide.with(itemView.travelingRepresentativeListImage)
                .load(string)
                .into(itemView.travelingRepresentativeListImage)
        }

        inner class TravelingInstagramShareViewHandler: TravelImagePickerViewHandler {
            override fun imagePicker() {
                val index = getViewModel().list.indexOf(fileNamePath)
                if (index != getViewModel().chooseIndex) {
                    getViewModel().choose[index].set(true)
                    getViewModel().choose[getViewModel().chooseIndex].set(false)
                    getViewModel().chooseIndex = index

                    Glide.with(travelingRepresentativeImage)
                        .load(fileNamePath)
                        .into(travelingRepresentativeImage)

                }
            }
        }
    }
}
