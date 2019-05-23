package com.kotlin.viaggio.data.obj

data class ViaggioApiAuth(
    var email: String = "",
    var name: String = "",
    val token: String,
    val AWS_IdentityId: String, val AWS_Token: String
)
//var imageUrl: String = "",

data class ViaggioApiAWSAuth(val AWS_IdentityId: String, val AWS_Token: String)
data class ViaggioApiTravelResult(var id: Int = 0)
data class ViaggioApiTravels(var travel: List<Travel>)
data class ViaggioApiTravelCards(var travelCard: List<TravelCard>)
data class ViaggioApiSync(var travelCount: Int = 0, var travelCardCount: Int = 0)


