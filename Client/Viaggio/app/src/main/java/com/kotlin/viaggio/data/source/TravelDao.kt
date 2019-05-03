package com.kotlin.viaggio.data.source

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import io.reactivex.Single

@Dao
interface TravelDao {
    // Travel
    @Insert
    fun insertTravel(travel: Travel): Single<Long>

    @Insert
    fun insertAllTravel(vararg travel: Travel)

    @Query("SELECT * FROM travels Order By startDate Asc")
    fun getTravels(): Single<List<Travel>>

    @Query("SELECT * FROM travels WHERE id IN(:id) limit 1")
    fun getTravel(id: Long): Single<Travel>

    @Update
    fun updateTravel(travel: Travel)

    // travelCard
    @Query("SELECT * FROM travelCards WHERE travelId IN(:travelId) ORDER BY travelOfDay Asc")
    fun getTravelCardAsc(travelId: Long): DataSource.Factory<Int, TravelCard>

    @Query("SELECT * FROM travelCards WHERE travelId IN(:travelId) ORDER BY travelOfDay DESC")
    fun getTravelCardDes(travelId: Long): DataSource.Factory<Int, TravelCard>

    @Insert
    fun insertTravelCard(travelCard: TravelCard):Single<Long>

    @Query("SELECT * FROM travelCards WHERE travelId IN(:travelOfDayId) ORDER BY date DESC")
    fun getTravelCardsPaged(travelOfDayId: Long): DataSource.Factory<Int, TravelCard>

    @Query("SELECT * FROM travelCards")
    fun getTravelCards(): Single<MutableList<TravelCard>>

    @Query("SELECT * FROM travelCards WHERE id IN(:travelCardId) limit 1")
    fun getTravelCard(travelCardId: Long): Single<List<TravelCard>>

    @Update
    fun updateTravelCard(travelCard: TravelCard)

}