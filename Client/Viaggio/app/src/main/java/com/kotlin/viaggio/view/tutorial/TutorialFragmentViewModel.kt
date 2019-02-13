package com.kotlin.viaggio.view.tutorial

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.kotlin.viaggio.data.model.Tutorial
import com.kotlin.viaggio.data.model.TutorialList
import com.kotlin.viaggio.view.common.BaseViewModel
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TutorialFragmentViewModel @Inject constructor() : BaseViewModel() {
    @Inject
    lateinit var gson: Gson

    val tutorialList: MutableLiveData<List<Tutorial>> = MutableLiveData()

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
}
