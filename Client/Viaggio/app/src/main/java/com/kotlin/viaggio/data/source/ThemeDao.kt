package com.kotlin.viaggio.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kotlin.viaggio.data.`object`.Theme
import io.reactivex.Single

@Dao
interface ThemeDao {
    @Insert
    fun insertTheme(theme: Theme): Single<Long>

    @Insert
    fun insertAllTheme(vararg Theme: Theme)

    @Query("SELECT * FROM themes")
    fun getThemes(): Single<List<Theme>>

    @Query("SELECT * FROM themes Where id In(:id)")
    fun getThemes(vararg id:Long): Single<List<Theme>>

    @Update
    fun updateTheme(theme: Theme)
}