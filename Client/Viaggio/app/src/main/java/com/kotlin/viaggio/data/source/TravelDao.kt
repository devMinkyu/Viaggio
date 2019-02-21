package com.kotlin.viaggio.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import io.reactivex.Flowable

@Dao
interface TravelDao {
    @Insert
    fun insertTravel(vararg travel: Travel)

    @Query("SELECT * FROM travels")
    fun getTravels(): Flowable<List<Travel>>

    @Insert
    fun insertTravelOfDay(vararg travel: TravelOfDay)

    @Insert
    fun insertTravelCard(vararg travel: TravelCard)
}