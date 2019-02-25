package com.kotlin.viaggio.view.traveling

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.kotlin.viaggio.R
import com.kotlin.viaggio.data.`object`.PermissionError
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_traveling.*
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
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
        binding.travelingList.layoutManager = LinearLayoutManager(context!!)
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

        getViewModel().travelOfDayListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {list ->
                travelingList.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                            = TravelOfDayViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_traveling, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as TravelOfDayViewHolder
                        holder.binding?.data = list[position]
                    }
                }
            }
        })
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
                        getViewModel().travelingStartOfDay.set(SimpleDateFormat(resources.getString(R.string.dateFormat)).format(cal.time))
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
    }
    inner class ThemeTravelingSelectedViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingSelectedThemeBinding>(view)
    }
    inner class TravelOfDayViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTravelingBinding>(view)
    }
}