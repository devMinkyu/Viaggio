package com.kotlin.viaggio.data.source

import androidx.room.*
import com.kotlin.viaggio.data.obj.Theme
import io.reactivex.Single

@Dao
interface ThemeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTheme(theme: Theme)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTheme(vararg Theme: Theme)

    @Query("SELECT * FROM themes")
    fun getThemes(): Single<List<Theme>>

    @Query("SELECT * FROM themes Where theme In(:theme)")
    fun getThemes(vararg theme:String): Single<List<Theme>>

    @Update
    fun updateTheme(theme: Theme)

    @Delete
    fun deleteTheme(theme: Theme)
}