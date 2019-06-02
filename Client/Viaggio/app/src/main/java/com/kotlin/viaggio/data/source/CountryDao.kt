package com.kotlin.viaggio.data.source

import androidx.room.*
import com.kotlin.viaggio.data.obj.Country
import io.reactivex.Single

@Dao
interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountry(country: Country)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCountry(vararg Country: Country)

    @Query("SELECT * FROM countries Where kind =:kind")
    fun getCountries(kind:Int): Single<List<Country>>

    @Update
    fun updateCountry(Country: Country)
}