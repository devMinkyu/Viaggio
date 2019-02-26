package com.kotlin.viaggio.data.`object`

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "travels")
data class Travel(
    @PrimaryKey(autoGenerate = true) var id:Long = 0,
    @Suppress("ArrayInDataClass") var countries:ArrayList<String> = arrayListOf(),
    var userId:String = "",
    var startOfDay:Date? = null,
    var endOfDay:Date? = null,
    @Suppress("ArrayInDataClass") var theme:ArrayList<String> = arrayListOf(),
    var themeImageName:String="",
    var share:Boolean = false
)


@Entity(tableName = "travelOfDays")
data class TravelOfDay(
    @PrimaryKey(autoGenerate = true) var id:Long = 0,
    var travelId:Long = 0,
    @Suppress("ArrayInDataClass") var countries:ArrayList<String> = arrayListOf(),
    @Suppress("ArrayInDataClass") var theme:ArrayList<String> = arrayListOf(),
    var day:Date? = null,
    var dayCount:Int = 1,
    var transportation:ArrayList<String> = arrayListOf(),
    var themeImageName: String = ""
)

@Entity(tableName = "travelCards")
data class TravelCard(
    @PrimaryKey(autoGenerate = true) var id:Long = 0,
    @Suppress("ArrayInDataClass") var imageNames:ArrayList<String> = arrayListOf(),
    @Suppress("ArrayInDataClass") var countries:ArrayList<String> = arrayListOf(),
    var travelOfDayId:Long = 0,
    var contents:String = "",
    var enrollOfTime:Date = Date(),
    var favorite:Int = 0,
    @Suppress("ArrayInDataClass") var previousTransportation:ArrayList<String> = arrayListOf()
)

data class Theme(var themes:MutableList<String>)