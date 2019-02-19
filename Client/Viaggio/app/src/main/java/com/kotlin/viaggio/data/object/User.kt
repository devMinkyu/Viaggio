package com.kotlin.viaggio.data.`object`

import androidx.room.PrimaryKey

data class User(
    @PrimaryKey
    var id:String = "",
    var name:String = "",
    var email:String ="",
    var profileUri:String = ""
)