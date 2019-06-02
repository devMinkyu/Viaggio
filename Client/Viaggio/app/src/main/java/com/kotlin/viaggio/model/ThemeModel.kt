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

    fun createThemes(themes: List<Theme>) =
        Completable.fromAction {
            db.get().themeDao().insertAllTheme(*themes.toTypedArray())
        }.subscribeOn(Schedulers.io())

    fun createTheme(theme: Theme) =
        Completable.fromAction {
            db.get().themeDao().insertTheme(theme)
        }.subscribeOn(Schedulers.io())


    fun deleteTheme(theme: Theme) =
        Completable.fromAction {
            db.get().themeDao().deleteTheme(theme)
        }.subscribeOn(Schedulers.io())

    fun getThemes():Single<List<Theme>> {
        return db.get().themeDao().getThemes().subscribeOn(Schedulers.io())
    }

    fun getDataFetchTheme() = api.getThemes().subscribeOn(Schedulers.io())
        .flatMap {
            if(it.isSuccessful) {
                it.body()?.let {body ->
                    Single.just(body.themes)
                }?:Single.just(listOf())
            } else {
                Single.just(listOf())
            }
        }
}