package com.kotlin.viaggio.data.`object`

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "travels")
data class Travel(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @Suppress("ArrayInDataClass") var travelOfDayIds:ArrayList<String> = arrayListOf(),
    @Suppress("ArrayInDataClass") var countries:ArrayList<String> = arrayListOf(),
    var userId:String = "",
    var startOfDay:Date? = null,
    var endOfDay:Date? = null,
    @Suppress("ArrayInDataClass") var theme:ArrayList<String> = arrayListOf(),
    var themeImageName:String=""
)


@Entity(tableName = "travelOfDays")
data class TravelOfDay(
    @PrimaryKey(autoGenerate = true) var id:Int = 0,
    @Suppress("ArrayInDataClass") var travelCardIds:ArrayList<String> = arrayListOf(),
    @Suppress("ArrayInDataClass") var countries:ArrayList<String> = arrayListOf(),
    var userId:String = "",
    var daily:String = "",
    var transportation:ArrayList<String> = arrayListOf(),
    var themeImageName: String = ""
)

@Entity(tableName = "travelCards")
data class TravelCard(
    @PrimaryKey(autoGenerate = true) var id:Int,
    @Suppress("ArrayInDataClass") var imageNames:ArrayList<String> = arrayListOf(),
    @Suppress("ArrayInDataClass") var countries:ArrayList<String> = arrayListOf(),
    var userId:String = "",
    var contents:String = "",
    var enrollOfTime:String = "",
    var favorite:Int = 0,
    @Suppress("ArrayInDataClass") var previousTransportation:ArrayList<String> = arrayListOf()
)

data class Theme(var themes:MutableList<String>)