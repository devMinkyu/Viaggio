package com.kotlin.viaggio.view.traveling.option

import android.graphics.drawable.GradientDrawable
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
import com.kotlin.viaggio.databinding.ItemThemeBinding
import com.kotlin.viaggio.view.common.BaseDialogFragment
import com.kotlin.viaggio.view.theme.ThemeViewHandler
import kotlinx.android.synthetic.main.fragment_action_dialog_traveling_themes.*
import org.jetbrains.anko.support.v4.toast

class TravelingThemesActionDialogFragment:BaseDialogFragment<TravelingThemesActionDialogFragmentViewModel>(){
    companion object {
        val TAG: String = TravelingThemesActionDialogFragment::class.java.simpleName
    }
    lateinit var binding:com.kotlin.viaggio.databinding.FragmentActionDialogTravelingThemesBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_action_dialog_traveling_themes, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawable = context?.getDrawable(R.drawable.dialog_bg) as GradientDrawable
        travelingThemesActionList.background = drawable
        travelingThemesActionList.clipToOutline = true
        travelingThemesActionList.layoutManager = FlexboxLayoutManager(context).apply {
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.CENTER
        }

        getViewModel().themesListLiveData.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                travelingThemesActionList.adapter = object : RecyclerView.Adapter<TravelingThemesViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TravelingThemesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false))
                    override fun getItemCount() = getViewModel().themeList.size
                    override fun onBindViewHolder(holder: TravelingThemesViewHolder, position: Int) {
                        holder.binding?.data = getViewModel().themeList[position]
                        holder.binding?.viewHandler = holder.TravelingThemesViewHandler()
                    }
                }
            }
        })

        getViewModel().completeLiveDate.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                dismiss()
            }
        })
    }

    inner class ViewHandler{
        fun cancel(){
            dismiss()
        }
        fun confirm(){
            getViewModel().confirm()
        }
    }

    inner class TravelingThemesViewHolder(view: View):RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<ItemThemeBinding>(view)
        inner class TravelingThemesViewHandler:ThemeViewHandler{
            override fun selected() {
                binding?.data?.let {
                    if(it.select.get()){
                        getViewModel().chooseThemesList.remove(it)
                        it.select.set(it.select.get().not())
                    }else{
                        if(getViewModel().chooseThemesList.size < 3){
                            getViewModel().chooseThemesList.add(it)
                            it.select.set(it.select.get().not())
                        }else{
                            toast("꽉참 경고문")
                        }
                    }
                }
            }
        }
    }
}