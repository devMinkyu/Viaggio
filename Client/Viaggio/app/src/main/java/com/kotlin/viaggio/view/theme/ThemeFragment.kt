package com.kotlin.viaggio.view.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.kotlin.viaggio.R
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_theme.*


class ThemeFragment:BaseFragment<ThemeFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentThemeBinding
    lateinit var adapter:RecyclerView.Adapter<RecyclerView.ViewHolder>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_theme, container, false)
        binding.viewModel = getViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexWrap = FlexWrap.WRAP
        themeList.layoutManager = layoutManager

        val layoutManager2 = FlexboxLayoutManager(context)
        layoutManager2.flexWrap = FlexWrap.WRAP
        themeSelectedList.layoutManager = layoutManager2

        getViewModel().themesList.observe(this, Observer {
            it.getContentIfNotHandled()?.let { theme ->
                themeList.adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                            = ThemeNonSelectedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_non_selected_theme, parent, false))
                    override fun getItemCount() = theme.themes.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as ThemeNonSelectedViewHolder
                        holder.binding?.data = theme.themes[position]
                        holder.binding?.viewHandler = ViewHandler()
                    }
                }
            }
        })
        adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                    = ThemeSelectedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_theme, parent, false))
            override fun getItemCount() = getViewModel().themes.themes.size
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as ThemeSelectedViewHolder
                holder.binding?.data = getViewModel().themes.themes[position]
                holder.binding?.viewHandler = ViewHandler()
            }
        }
        themeSelectedList.adapter = adapter
    }

    inner class ViewHandler{
        fun chooseTheme(theme:String){
            if(getViewModel().themes.themes.contains(theme).not()){
                getViewModel().chooseTheme(theme)
                adapter.notifyItemInserted(getViewModel().themes.themes.size - 1)
            }
        }
        fun cancelTheme(theme:String){
            val position = getViewModel().themes.themes.indexOf(theme)
            adapter.notifyItemRemoved(position)
            getViewModel().cancelTheme(theme)
        }
    }
    inner class ThemeNonSelectedViewHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemNonSelectedThemeBinding>(view)
    }
    inner class ThemeSelectedViewHolder(view:View):RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemSelectedThemeBinding>(view)
    }
}
