package com.kotlin.viaggio.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import io.reactivex.Single

@Dao
interface TravelDao {
    @Insert
    fun insertTravel(travel: Travel): Single<Long>

    @Insert
    fun insertAllTravel(vararg travel: Travel)

    @Query("SELECT * FROM travels")
    fun getTravels(): Single<List<Travel>>

    @Insert
    fun insertTravelOfDay(travelOfDay: TravelOfDay): Single<Long>

    @Query("SELECT * FROM travelOfDays WHERE travelId IN(:travelId) ORDER BY day DESC")
    fun getTravelOfDays(travelId: Long): Single<List<TravelOfDay>>

    @Insert
    fun insertTravelCard(travelCard: TravelCard): Single<Long>

    @Query("SELECT * FROM travelOfDays")
    fun test(): Single<List<TravelOfDay>>
}