package com.kotlin.viaggio.data.obj

import androidx.databinding.ObservableInt
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "travels")
data class Travel(
    @PrimaryKey var localId: Long = 0,
    var serverId: Int = 0,
    @Suppress("ArrayInDataClass") var area: MutableList<Area> = mutableListOf(),
    var userExist: Boolean = false,
    var title: String = "재미있는 여행",
    // 0 해외여행, 1 국내여행, 2 당일치기
    var travelKind:Int =0,
    var startDate: Date? = null,
    var endDate: Date? = null,
    @Suppress("ArrayInDataClass") var theme: MutableList<String> = mutableListOf(),
    var imageName: String = "",
    var imageUrl: String = "",
    var share: Boolean = false,
    @field:JvmField var isDelete:Boolean = false
)
data class TravelBodyList(var travels: List<TravelBody>)
data class TravelBody(
    var localId: Long = 0,
    var serverId: Int = 0,
    @Suppress("ArrayInDataClass") var area: MutableList<Area> = mutableListOf(),
    var title: String = "",
    // 0 해외여행, 1 국내여행, 2 당일치기
    var travelKind:Int =0,
    var startDate: String? = null,
    var endDate: String? = null,
    @Suppress("ArrayInDataClass") var theme: MutableList<String> = mutableListOf(),
    var imageName: String = "",
    var imageUrl: String = "",
    var share: Boolean = false,
    @field:JvmField var isDelete:Boolean = false
)

data class Traveled(
    var id: Long = 0,
    var theme:String = "",
    var period:String = "",
    var title:String ="",
    var countries:String = ""
)
data class TravelCardBodyList(var travelCards: List<TravelCardBody>)
@Entity(tableName = "travelCards")
data class TravelCard(
    @PrimaryKey var localId: Long = 0,
    var serverId: Int = 0,
    var userExist: Boolean = false,
    var travelLocalId: Long = 0,
    var travelServerId: Int = 0,
    var travelOfDay: Int = 1,
    var country:String = "",
    @Suppress("ArrayInDataClass") var theme: MutableList<String> = mutableListOf(),
    @Suppress("ArrayInDataClass") var imageNames: MutableList<String> = mutableListOf(),
    @Suppress("ArrayInDataClass") var imageUrl: MutableList<String> = mutableListOf(),
    var content: String = "",
    var date: Date = Date(),
    @field:JvmField var isDelete:Boolean = false
)
data class TravelCardBody(
    var localId: Long = 0,
    var serverId: Int = 0,
    var travelLocalId: Long = 0,
    var travelServerId: Int = 0,
    var travelOfDay: Int = 1,
    var country:String = "",
    @Suppress("ArrayInDataClass") var theme: MutableList<String> = mutableListOf(),
    @Suppress("ArrayInDataClass") var imageNames: MutableList<String> = mutableListOf(),
    @Suppress("ArrayInDataClass") var imageUrl: MutableList<String> = mutableListOf(),
    var content: String = "",
    var date: String = "",
    @field:JvmField var isDelete:Boolean = false
)

data class ImageData(
    var imageName: String = "",
    var chooseCountList:ObservableInt = ObservableInt(0)
)

data class TravelCardValue(
    var id: Long = 0,
    var travelId: Long = 0,
    var travelOfDay: Int = 1,
    var country:String = "",
    var theme: String = "",
    var imageName: String = "",
    var content: String = ""
)