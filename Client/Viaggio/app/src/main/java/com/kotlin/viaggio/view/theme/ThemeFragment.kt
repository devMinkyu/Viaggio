package com.kotlin.viaggio.view.theme

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kotlin.viaggio.R
import com.kotlin.viaggio.android.ArgName
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_theme.*
import kotlinx.android.synthetic.main.item_theme.view.*
import org.jetbrains.anko.support.v4.toast


class ThemeFragment:BaseFragment<ThemeFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentThemeBinding
    lateinit var adapter:RecyclerView.Adapter<ThemeViewHolder>
    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            getViewModel().option = it.getBoolean(ArgName.TRAVEL_OPTION.name, false)
        }
    }
    override fun onResume() {
        super.onResume()
//        if(getViewModel().option.not()){
//            if(sliderInterface == null)
//                sliderInterface = Slidr.replace(container, SlidrConfig.Builder().position(
//                    SlidrPosition.TOP)
//                    .build())
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_theme, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER
        themeList.layoutManager = layoutManager

        if(getViewModel().option){
            enableSliding(false)
        }

        getViewModel().themesListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { theme ->
                adapter = object : RecyclerView.Adapter<ThemeViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        ThemeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false))
                    override fun getItemCount() = getViewModel().themeList.size

                    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
                        if(position < getViewModel().themeList.size){
                            holder.binding?.data = getViewModel().themeList[position]
                            holder.binding?.viewHandler = holder.ThemesViewHandler()
                            holder.itemView.themeName.setOnCloseIconClickListener {
                                val index = getViewModel().themeList.indexOf(holder.binding!!.data)
                                if(index < adapter.itemCount) {
                                    getViewModel().removeCustomTheme(getViewModel().themeList[index], index)
                                }

                            }
                        }

                    }
                }
                themeList.adapter = adapter
            }
        })
        getViewModel().completeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                stopLoading()
                fragmentPopStack()
            }
        })
        getViewModel().addLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                adapter.notifyItemInserted(getViewModel().themeList.size - 1)
            }
        })
        getViewModel().removeLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {index ->
                adapter.notifyItemRemoved(index)
            }
        })

        custom_theme.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    getViewModel().createCustomTheme()
                    adapter.notifyItemInserted(getViewModel().themeList.size)
                    false
                }
                else -> {
                    getViewModel().createCustomTheme()
                    false
                }
            }
        }
    }

    inner class ViewHandler{
        fun confirm(){
            if(getViewModel().option){
                if(getViewModel().selectedTheme.isEmpty()){
                    toast(resources.getString(R.string.empty_country_hint))
                }else{
                    showLoading()
                    getViewModel().sendTheme()
                }
            }else{
                getViewModel().sendTheme()
            }
        }
        fun close(){
            fragmentPopStack()
        }
        fun themeClear() {
            getViewModel().customTheme.set("")
        }
    }
    inner class ThemeViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemThemeBinding>(view)

        inner class ThemesViewHandler: ThemeViewHandler{
            override fun selected(){
                binding?.let {
                    if(it.data!!.select.get()){
                        if(getViewModel().selectedTheme.contains(it.data)){
                            getViewModel().selectedTheme.remove(it.data)
                        }
                    }else{
                        getViewModel().selectedTheme.add(it.data)
                    }
                    it.data!!.select.set(it.data!!.select.get().not())
                }
            }
        }
    }
}