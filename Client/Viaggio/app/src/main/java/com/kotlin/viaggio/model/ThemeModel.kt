package com.kotlin.viaggio.model

import com.kotlin.viaggio.data.`object`.Theme
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeModel @Inject constructor() :BaseModel(){

    fun createThemes(thems:List<Theme>):Completable{
        return Completable.fromAction{
            db.get()
        }
    }
    fun createTheme(thems:Theme):Completable{
        return Completable.fromAction{
            db.get()
        }
    }
    fun getThemes(){

    }
    fun getThemes(id:List<Long>){

    }
    fun updateTheme(){

    }
}