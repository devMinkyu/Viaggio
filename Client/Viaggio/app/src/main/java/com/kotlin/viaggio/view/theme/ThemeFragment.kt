package com.kotlin.viaggio.view.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_theme.*


class ThemeFragment:BaseFragment<ThemeFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentThemeBinding
    override fun onResume() {
        super.onResume()
        if(sliderInterface == null)
            sliderInterface = Slidr.replace(container, SlidrConfig.Builder().position(
                SlidrPosition.TOP)
                .build())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_theme, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER
        themeList.layoutManager = layoutManager

        getViewModel().themesListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { theme ->
                themeList.adapter = object : RecyclerView.Adapter<ThemeViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        ThemeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false))
                    override fun getItemCount() = theme.size

                    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
                        holder.binding?.data = theme[position]
                        holder.binding?.viewHandler = holder.ThemeViewHandler()
                    }
                }
            }
        })

//        themeList.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            themeList?.let {
//                if (themeList.canScrollVertically(-1).not()) {
//                    enableSliding(true)
//                } else {
//                    enableSliding(false)
//                }
//            }
//        }
    }

    inner class ViewHandler{
        fun confirm(){
            getViewModel().sendTheme()
            fragmentPopStack()
        }
        fun close(){
            fragmentPopStack()
        }
    }
    inner class ThemeViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemThemeBinding>(view)

        inner class ThemeViewHandler{
            fun selected(){
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