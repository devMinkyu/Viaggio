package com.kotlin.viaggio.view.tutorial

import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.model.Tutorial
import com.kotlin.viaggio.view.common.BaseViewModel
import javax.inject.Inject

class TutorialFragmentViewModel @Inject constructor():BaseViewModel() {
    val tutorialList:MutableLiveData<List<Tutorial>> = MutableLiveData()

    override fun initialize() {
        super.initialize()

        val list:MutableList<Tutorial> = mutableListOf()

        val item = Tutorial("One", "good viewPager")
        list.add(item)
        val item1 = Tutorial("Two", "good viewPager1")
        list.add(item1)
        val item2 = Tutorial("Three", "good viewPager2")
        list.add(item2)
        val item3 = Tutorial("Four", "good viewPager3")
        list.add(item3)

        tutorialList.value = list
    }
}
