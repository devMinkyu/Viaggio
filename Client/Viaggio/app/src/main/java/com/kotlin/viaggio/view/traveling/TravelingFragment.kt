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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.data.`object`.TravelOfDay
import com.kotlin.viaggio.data.`object`.TravelingError
import com.kotlin.viaggio.view.common.BaseFragment
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

        travelingList.layoutManager = LinearLayoutManager(context!!)
        if(getViewModel().traveling.get()){
            fetchList()
        }
        getViewModel().travelStartLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                fetchList()
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
    }

    private fun fetchList(){
        val adapter = TravelOfDayAdapter()
        travelingList.adapter = adapter
        getViewModel().travelOfDayPagedLiveData.observe(this, Observer(adapter::submitList))
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
                        getViewModel().travelingStartOfDay.set(SimpleDateFormat(resources.getString(R.string.date_format)).format(cal.time))
                    }
                    cancelButton {
                        it.dismiss()
                    }
                }
            }.show()
        }
        fun travelStart(){
            getViewModel().travelStart()
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
    }

    inner class ThemeTravelingSelectedViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingSelectedThemeBinding>(view)
    }

    inner class TravelOfDayAdapter:PagedListAdapter<TravelOfDay, TravelOfDayViewHolder>(object :DiffUtil.ItemCallback<TravelOfDay>(){
        override fun areItemsTheSame(oldItem: TravelOfDay, newItem: TravelOfDay) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TravelOfDay, newItem: TravelOfDay) = oldItem == newItem
    }){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                = TravelOfDayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling, parent, false))

        override fun onBindViewHolder(holder: TravelOfDayViewHolder, position: Int) {
            val imgDir = File(context?.filesDir, "images/")
            holder.binding?.data = getItem(position)
            holder.binding?.viewHandler = holder.ViewHandler()
            getItem(position)?.themeImageName?.let {
                val imgFile = File(imgDir, it)
                if (imgFile.exists()) {
                    Uri.fromFile(imgFile).let { uri ->
                        Glide.with(holder.itemView.traveledBackground)
                            .load(uri)
                            .into(holder.itemView.traveledBackground)
                    }
                }
            }
        }
    }
    inner class TravelOfDayViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingBinding>(view)

        inner class ViewHandler{
            fun selectedTravelOfDay(){
                val id = binding?.data?.id?:0
                baseIntent("http://viaggio.kotlin.com/traveling/$id/detail/")
            }
        }
    }
}