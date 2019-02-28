package com.kotlin.viaggio.view.theme

import android.graphics.Color
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
import kotlinx.android.synthetic.main.fragment_theme.*
import org.jetbrains.anko.backgroundColor


class TravelingOfDayThemeFragment:BaseFragment<TravelingOfDayThemeFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentTravelingOfDayThemeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_traveling_of_day_theme, container, false)
        binding.viewModel = getViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.CENTER
        themeList.layoutManager = layoutManager

        getViewModel().themesList.observe(this, Observer {
            it.getContentIfNotHandled()?.let { theme ->
                themeList.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                            = ThemeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false))
                    override fun getItemCount() = theme.themes.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as ThemeViewHolder
                        holder.binding?.data = theme.themes[position]

                        holder.binding?.themeName?.setOnClickListener {
                            if(getViewModel().themes.themes.contains(theme.themes[position])){
                                getViewModel().cancelTheme(theme.themes[position])
                                holder.itemView.backgroundColor = Color.WHITE
                            }else{
                                holder.itemView.backgroundColor = resources.getColor(R.color.colorPrimary, null)
                                getViewModel().sendTheme(theme.themes[position])
                            }
                        }
                    }
                }
            }
        })

        getViewModel().complete.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                fragmentPopStack()
            }
        })
    }

    inner class ThemeViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemThemeBinding>(view)
    }
}
