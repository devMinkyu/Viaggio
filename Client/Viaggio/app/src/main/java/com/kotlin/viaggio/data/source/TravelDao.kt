package com.kotlin.viaggio.data.source

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kotlin.viaggio.data.obj.Travel
import com.kotlin.viaggio.data.obj.TravelCard
import io.reactivex.Single

@Dao
interface TravelDao {
    // Travel
    @Insert
    fun insertTravel(vararg travel: Travel)

    @Insert
    fun insertAllTravel(vararg travel: Travel)

    @Query("SELECT * FROM travels Where isDelete = 0 Order By startDate Asc")
    fun getTravels(): Single<List<Travel>>

    @Query("SELECT * FROM travels")
    fun getNotUploadTravels(): Single<List<Travel>>

    @Query("SELECT * FROM travels WHERE localId IN(:id) And isDelete = 0 limit 1")
    fun getTravel(id: Long): Single<Travel>

    @Update
    fun updateTravel(vararg travel: Travel)

    // travelCard
    @Query("SELECT * FROM travelCards WHERE travelLocalId IN(:travelId) And isDelete = 0 ORDER BY travelOfDay, date Asc")
    fun getTravelCardAsc(travelId: Long): DataSource.Factory<Int, TravelCard>

    @Query("SELECT * FROM travelCards WHERE travelLocalId IN(:travelId) And isDelete = 0 ORDER BY travelOfDay DESC, date DESC")
    fun getTravelCardDes(travelId: Long): DataSource.Factory<Int, TravelCard>

    @Insert
    fun insertTravelCard(vararg travelCard: TravelCard)

    @Query("SELECT * FROM travelCards WHERE travelLocalId IN(:travelOfDayId) And isDelete = 0 ORDER BY date DESC")
    fun getTravelCardsPaged(travelOfDayId: Long): DataSource.Factory<Int, TravelCard>

    @Query("SELECT * FROM travelCards Where isDelete = 0 And travelLocalId = :travelId")
    fun getTravelCards(travelId: Long): Single<MutableList<TravelCard>>

    @Query("SELECT * FROM travelCards Where isDelete = 0")
    fun getTravelCards(): Single<MutableList<TravelCard>>

    @Query("SELECT * FROM travelCards")
    fun getNotUploadTravelCards(): Single<MutableList<TravelCard>>

    @Query("SELECT * FROM travelCards WHERE localId IN(:travelCardId) And isDelete = 0 limit 1")
    fun getTravelCard(travelCardId: Long): Single<List<TravelCard>>

    @Update
    fun updateTravelCard(vararg travelCard: TravelCard)

}