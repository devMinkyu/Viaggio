package com.kotlin.viaggio.data.source

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kotlin.viaggio.data.`object`.Travel
import com.kotlin.viaggio.data.`object`.TravelCard
import com.kotlin.viaggio.data.`object`.TravelOfDay
import io.reactivex.Completable
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
    fun updateTravel(travel: Travel):Completable

    // travelOfDay
    @Insert
    fun insertTravelOfDay(travelOfDay: TravelOfDay): Single<Long>

    @Insert
    fun insertAllTravelOfDay(vararg travelOfDay: TravelOfDay): Single<MutableList<Long>>

    @Query("SELECT * FROM travelCards WHERE travelId IN(:travelId) ORDER BY date Asc")
    fun getTravelCardAsc(travelId: Long): DataSource.Factory<Int, TravelCard>

    @Query("SELECT * FROM travelCards WHERE travelId IN(:travelId) ORDER BY date DESC")
    fun getTravelCardDes(travelId: Long): DataSource.Factory<Int, TravelCard>

    @Query("SELECT * FROM travelOfDays WHERE id = :id limit 1")
    fun getTravelOfDay(id: Long): Single<TravelOfDay>

    @Query("SELECT * FROM travelOfDays WHERE travelOfDay = :day And travelId = :travelId limit 1")
    fun getTravelOfDayCount(day: Int, travelId: Long): Single<TravelOfDay>

    @Update
    fun updateTravelOfDay(travelOfDay: TravelOfDay)

    // travelCard
    @Insert
    fun insertTravelCard(travelCard: TravelCard)

    @Query("SELECT * FROM travelCards WHERE travelId IN(:travelOfDayId) ORDER BY date DESC")
    fun getTravelCardsPaged(travelOfDayId: Long): DataSource.Factory<Int, TravelCard>

    @Query("SELECT * FROM travelCards")
    fun getTravelCards(): Single<MutableList<TravelCard>>

    @Query("SELECT * FROM travelCards WHERE travelId IN(:travelId) limit 1")
    fun getTravelCard(travelId: Long): Single<TravelCard>

    @Update
    fun updateTravelCard(travelCard: TravelCard)

}