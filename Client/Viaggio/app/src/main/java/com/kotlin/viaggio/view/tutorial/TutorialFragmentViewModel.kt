package com.kotlin.viaggio.view.tutorial

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.kotlin.viaggio.data.obj.Tutorial
import com.kotlin.viaggio.data.obj.TutorialList
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.view.common.BaseViewModel
import java.io.InputStreamReader
import javax.inject.Inject

class TutorialFragmentViewModel @Inject constructor() : BaseViewModel() {

    val tutorialList: MutableLiveData<List<Tutorial>> = MutableLiveData()

    val showButton = ObservableBoolean(false)
    override fun initialize() {
        super.initialize()

        val inputStream = InputStreamReader(appCtx.get().assets.open("tutorial.json"))
        val tutorials: TutorialList = gson.fromJson(inputStream, TutorialList::class.java)

        val list: MutableList<Tutorial> = mutableListOf()
        for (datum in tutorials.data) {
            list.add(datum)
        }
        tutorialList.value = list
    }
    fun tutorialEnd() {
        prefUtilService.putBool(AndroidPrefUtilService.Key.TUTORIAL_CHECK, true).blockingAwait()
    }
}
