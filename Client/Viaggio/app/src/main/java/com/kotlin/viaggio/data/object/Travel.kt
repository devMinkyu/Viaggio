package com.kotlin.viaggio.data.`object`

data class Travel(
    var id:String = "",
    var travelOfDayIds:MutableList<String> = mutableListOf(),
    var countries:MutableList<String> = mutableListOf(),
    var userId:String = "",
    var startOfDay:String = "",
    var endOfDay:String = "",
    var themeTitle:String ="",
    var themeImgUri:String=""
)

data class TravelOfDay(
    var id:String = "",
    var travelCardIds:MutableList<String> = mutableListOf(),
    var countries:MutableList<String> = mutableListOf(),
    var userId:String = "",
    var daily:String = "",
    var transportation:MutableList<String> = mutableListOf(),
    var themeImgUri: String = ""
)

data class TravelCard(
    var id:String = "",
    var imageUris:MutableList<String> = mutableListOf(),
    var countries:MutableList<String> = mutableListOf(),
    var userId:String = "",
    var contents:String = "",
    var enrollOfTime:String = "",
    var favorite:Int = 0,
    var previousTransportation:MutableList<String> = mutableListOf()
)