package com.kotlin.viaggio.view.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kotlin.viaggio.R
import com.kotlin.viaggio.extenstions.baseIntent
import com.kotlin.viaggio.view.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_tutorial.*
import kotlinx.android.synthetic.main.item_tutorial.view.*

class TutorialFragment:BaseFragment<TutorialFragmentViewModel>() {
    private lateinit var binding:com.kotlin.viaggio.databinding.FragmentTutorialBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial, container, false)
        binding.viewModel = getViewModel()
        binding.viewHandler = ViewHandler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        tutorialPager.clipToPadding =false
//        tutorialPager.setPadding(40,0,40,0)
        getViewModel().tutorialList.observe(this, Observer {list ->
                tutorialPager.adapter = object: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                        TutorialViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tutorial, parent, false))
                    override fun getItemCount() = list.size
                    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                        holder as TutorialViewHolder
                        if(position < list.size){
                            holder.binding?.data = list[position]
                            holder.itemView.tutorialAnim.setAnimation(list[position].animRes)
                        }
                    }
                }
            tutorialPagerIndicator.setViewPager2(tutorialPager)
                tutorialPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(position: Int) {
                        if (position == list.size - 1) {
                            getViewModel().showButton.set(true)
                        } else {
                            getViewModel().showButton.set(false)
                        }
                    }
                })

        })
    }

    inner class ViewHandler{
        fun skip(){
            getViewModel().tutorialEnd()
            baseIntent("http://viaggio.kotlin.com/home/main/")
            parentFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        fun login(){
            getViewModel().tutorialEnd()
            baseIntent("http://viaggio.kotlin.com/login/normal/")
        }
    }

    inner class TutorialViewHolder(view:View):RecyclerView.ViewHolder(view){
        val binding = DataBindingUtil.bind<com.kotlin.viaggio.databinding.ItemTutorialBinding>(view)
    }
}