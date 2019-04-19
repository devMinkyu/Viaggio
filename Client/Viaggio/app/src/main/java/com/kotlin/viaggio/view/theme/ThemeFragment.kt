package com.kotlin.viaggio.view.theme

import android.graphics.Rect
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
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_theme.*


class ThemeFragment:BaseFragment<ThemeFragmentViewModel>() {
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentThemeBinding
    lateinit var adapter:RecyclerView.Adapter<RecyclerView.ViewHolder>
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
        themeList.addItemDecoration(ThemeItemDecoration())

        getViewModel().themesListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let { theme ->
                adapter = object :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun getItemViewType(position: Int): Int {
                        return if(getViewModel().themes.themes.contains(theme.themes[position])) 1 else 0
                    }
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
                        0 -> {
                            ThemeNonSelectedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_non_selected_theme, parent, false))
                        }
                        1 -> {
                            ThemeSelectedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_selected_theme, parent, false))
                        }
                        else -> {
                            ThemeNonSelectedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_non_selected_theme, parent, false))
                        }
                    }
                    override fun getItemCount() = theme.themes.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        when(holder){
                            is ThemeSelectedViewHolder -> {
                                holder.binding?.data = theme.themes[position]
                                holder.binding?.viewHandler = ViewHandler()
                            }
                            is ThemeNonSelectedViewHolder ->{
                                holder.binding?.data = theme.themes[position]
                                holder.binding?.viewHandler = ViewHandler()
                            }
                        }
                    }
                }
                themeList.adapter = adapter
            }
        })

        getViewModel().showSelectTheme.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                adapter.notifyDataSetChanged()
            }
        })
    }

    inner class ViewHandler{
        fun confirm(){
            getViewModel().sendTheme()
            fragmentPopStack()
        }
        fun close(){
            fragmentPopStack()
        }
        fun chooseTheme(theme:String){
            if(getViewModel().themes.themes.contains(theme).not()){
                getViewModel().chooseTheme(theme)
                val index = getViewModel().themesList.themes.indexOf(theme)
                adapter.notifyItemChanged(index)
            }
        }
        fun cancelTheme(theme:String){
            val index = getViewModel().themesList.themes.indexOf(theme)
            adapter.notifyItemChanged(index)
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

class ThemeItemDecoration :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val adapterPos = parent.getChildAdapterPosition(view)

        if (adapterPos < 3) {
                val firstHorMarginVal1 = (parent.context.resources.getDimension(R.dimen.theme_height))
                outRect.top = firstHorMarginVal1.toInt()

        }
    }
}