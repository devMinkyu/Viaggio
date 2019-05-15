package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.obj.Theme
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeModel @Inject constructor() : BaseModel() {

    fun createThemes(thems: List<Theme>) =
        Completable.fromAction {
            db.get()
        }

    fun createTheme(theme: Theme) =
        Completable.fromAction {
            db.get()
        }

    fun getThemes() {

    }

    fun getThemes(themes: List<String>) {

    }

    fun updateTheme(theme: Theme) =
        Completable.fromAction {

        }

}