package com.kotlin.viaggio.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kotlin.viaggio.data.obj.Country
import io.reactivex.Single

@Dao
interface CountryDao {
    @Insert
    fun insertCountry(country: Country)

    @Insert
    fun insertAllCountry(vararg Country: Country)

    @Query("SELECT * FROM countries")
    fun getCountrys(): Single<List<Country>>

    @Update
    fun updateCountry(Country: Country)
}