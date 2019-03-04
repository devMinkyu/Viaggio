package com.kotlin.viaggio.view.traveling.detail

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
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling_representative_image.*
import kotlinx.android.synthetic.main.item_traveling_representative_image.view.*
import java.io.File


class TravelingRepresentativeImageFragment : BaseFragment<TravelingRepresentativeImageFragmentViewModel>() {
    lateinit var binding: com.kotlin.viaggio.databinding.FragmentTravelingRepresentativeImageBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_representative_image, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        travelingRepresentativeImageList.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        getViewModel().imageNamesListLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let { imageNames ->
                val imgDir = File(context?.filesDir, "images/")
                if (imgDir.exists()) {
                    val imgFile = File(imgDir, imageNames[0])
                    if (imgFile.exists()) {
                        Uri.fromFile(imgFile).let { uri ->
                            Glide.with(travelingRepresentativeImage)
                                .load(uri)
                                .into(travelingRepresentativeImage)
                        }
                    }
                    travelingRepresentativeImageList.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                            TravelingRepresentativeImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling_representative_image, parent, false))
                        override fun getItemCount() = imageNames.size

                        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                            holder as TravelingRepresentativeImageViewHolder
                            holder.binding?.viewHandler = holder.TravelingRepresentativeImageViewHandler()
                            holder.binding?.choose = getViewModel().choose[position]
                            holder.imageBinding(imageNames[position])
                        }
                    }
                }
            }
        })

    }

    inner class ViewHandler {
        fun confirm() {
        }

        fun back() {
            fragmentPopStack()
        }
    }

    inner class TravelingRepresentativeImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingRepresentativeImageBinding>(view)
        private lateinit var fileNamePath: String
        private val imgDir = File(context?.filesDir, "images/")

        fun imageBinding(string: String) {
            fileNamePath = string
            val layoutParams = itemView.travelingRepresentativeContainer.layoutParams
            layoutParams.width = width / 4
            layoutParams.height = width / 4
            itemView.travelingRepresentativeContainer.layoutParams = layoutParams

            val imgFile = File(imgDir, string)
            if (imgFile.exists()) {
                Uri.fromFile(imgFile).let { uri ->
                    Glide.with(itemView.travelingRepresentativeListImage)
                        .load(uri)
                        .into(itemView.travelingRepresentativeListImage)
                }
            }
        }

        inner class TravelingRepresentativeImageViewHandler{
            fun imagePicker(){

            }
        }
    }
}