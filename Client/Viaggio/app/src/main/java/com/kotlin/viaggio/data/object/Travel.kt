package com.kotlin.viaggio.data.`object`

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "travels")
data class Travel(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @Suppress("ArrayInDataClass") var entireCountries: ArrayList<String> = arrayListOf(),
    var userExist: Boolean = false,
    var title: String = "재미있는 여행",
    var travelType:String ="",
    var startDate: Date? = null,
    var endDate: Date? = null,
    @Suppress("ArrayInDataClass") var theme: ArrayList<String> = arrayListOf(),
    var backgroundImageName: String = "",
    var backgroundImageUrl: String = "",
    var share: Boolean = false,
    var isDelete:Boolean = false
)

data class Traveled(
    var id: Long = 0,
    var theme:String = "",
    var period:String = "",
    var title:String ="",
    var countries:String = ""
)

@Entity(tableName = "travelOfDays")
data class TravelOfDay(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var travelId: Long = 0,
    @Suppress("ArrayInDataClass") var dayCountries: ArrayList<String> = arrayListOf(),
    @Suppress("ArrayInDataClass") var theme: ArrayList<String> = arrayListOf(),
    var date: Date = Date(),
    var travelOfDay: Int = 1,
    var themeImageName: String = ""
)

data class TravelOfDayVal(
    var id:Long = 0,
    var dayCount:Int = 0,
    var week:String = "",
    var day:String = "",
    var countries: String = "",
    var weather:String = "",
    var weekend:Int = -1
)

@Entity(tableName = "travelCards")
data class TravelCard(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var travelId: Long = 0,
    var travelOfDay: Int = 1,
    var country:String = "",
    @Suppress("ArrayInDataClass") var imageNames: ArrayList<String> = arrayListOf(),
    @Suppress("ArrayInDataClass") var imageUrl: ArrayList<String> = arrayListOf(),
    var title: String = "",
    var content: String = "",
    var date: Date = Date()
)

data class Theme(
    var themes: MutableList<String> = mutableListOf(),
    var authority:Boolean = false
)