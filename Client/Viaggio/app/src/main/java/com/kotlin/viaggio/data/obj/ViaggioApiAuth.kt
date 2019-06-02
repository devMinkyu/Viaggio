package com.kotlin.viaggio.data.obj

data class ViaggioApiAuth(
    var email: String = "",
    var name: String = "",
    val token: String,
    val AWS_IdentityId: String, val AWS_Token: String
)
//var imageUrl: String = "",
data class ViaggioApiTravelsData(var localId: Long = 0, var serverId: Int)
data class ViaggioApiAWSAuth(val AWS_IdentityId: String, val AWS_Token: String)
data class ViaggioApiTravelResult(var id: Int = 0)
data class ViaggioApiTravelsResult(var travels:List<ViaggioApiTravelsData>)
data class ViaggioApiTravels(var travels: List<TravelBody>)
data class ViaggioApiTravelCards(var travelCards: List<TravelCardBody>)
data class ViaggioApiSync(var travelCount: Int = 0, var travelCardCount: Int = 0)
data class ViaggioApiSyncId(val localId: Long, val serverId: Int)

data class ViaggioApiTheme(var themes:List<Theme>)
data class ViaggioApiCountry(var contries: List<Country>)
data class ViaggioApiDomestics(var domestics: List<Country>)


