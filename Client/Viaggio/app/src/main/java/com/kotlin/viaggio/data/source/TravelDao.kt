package com.kotlin.viaggio.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface TravelDao {
    @Insert
    fun insertTravel(travel: Travel):Single<Long>
    @Insert
    fun insertAllTravel(vararg travel: Travel)

    @Query("SELECT * FROM travels")
    fun getTravels(): Flowable<List<Travel>>

    @Insert
    fun insertTravelOfDay(travel: TravelOfDay):Single<Long>

    @Insert
    fun insertTravelCard(travel: TravelCard):Single<Long>
}