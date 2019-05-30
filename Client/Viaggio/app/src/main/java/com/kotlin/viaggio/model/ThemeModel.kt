package com.kotlin.viaggio.model

import com.google.gson.Gson
import com.kotlin.viaggio.data.obj.Theme
import com.kotlin.viaggio.data.source.AndroidPrefUtilService
import com.kotlin.viaggio.data.source.ViaggioApiService
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeModel @Inject constructor() : BaseModel() {
    @Inject
    lateinit var prefUtilService: AndroidPrefUtilService
    @Inject
    lateinit var api: ViaggioApiService
    @Inject
    lateinit var gson: Gson

    fun createThemes(thems: List<Theme>) =
        Completable.fromAction {
            db.get().themeDao().insertAllTheme(*thems.toTypedArray())
        }.subscribeOn(Schedulers.io())

    fun createTheme(theme: Theme) =
        Completable.fromAction {
            db.get().themeDao().insertTheme(theme)
        }.subscribeOn(Schedulers.io())

    fun getThemes():Single<List<Theme>> {
        return db.get().themeDao().getThemes().subscribeOn(Schedulers.io())
    }

    fun getThemes(themes: List<String>) {

    }

    fun updateTheme(theme: Theme) =
        Completable.fromAction {

        }

    fun getDataFetchTheme() = api.getThemes().subscribeOn(Schedulers.io())
        .flatMap {
            if(it.isSuccessful) {
                Single.just(it.body()!!.themes)
            } else {
                Single.just(listOf())
            }
        }
}