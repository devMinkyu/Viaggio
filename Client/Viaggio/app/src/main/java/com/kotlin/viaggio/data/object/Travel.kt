package com.kotlin.viaggio.data.`object`

import androidx.room.PrimaryKey

data class Travel(
    @PrimaryKey var id:String = "",
    @Suppress("ArrayInDataClass") var travelOfDayIds:MutableList<String> = mutableListOf(),
    @Suppress("ArrayInDataClass") var countries:MutableList<String> = mutableListOf(),
    var userId:String = "",
    var startOfDay:String = "",
    var endOfDay:String = "",
    var themeTitle:String ="",
    var themeImgUri:String=""
)

data class TravelOfDay(
    @PrimaryKey var id:String = "",
    @Suppress("ArrayInDataClass") var travelCardIds:MutableList<String> = mutableListOf(),
    @Suppress("ArrayInDataClass") var countries:MutableList<String> = mutableListOf(),
    var userId:String = "",
    var daily:String = "",
    var transportation:MutableList<String> = mutableListOf(),
    var themeImgUri: String = ""
)

data class TravelCard(
    @PrimaryKey var id:String = "",
    @Suppress("ArrayInDataClass") var imageUris:MutableList<String> = mutableListOf(),
    @Suppress("ArrayInDataClass") var countries:MutableList<String> = mutableListOf(),
    var userId:String = "",
    var contents:String = "",
    var enrollOfTime:String = "",
    var favorite:Int = 0,
    @Suppress("ArrayInDataClass") var previousTransportation:MutableList<String> = mutableListOf()
)