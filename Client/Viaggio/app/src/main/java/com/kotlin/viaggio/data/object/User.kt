package com.kotlin.viaggio.data.`object`

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var id:String = "",
    var name:String = "",
    var email:String ="",
    var profileUri:String = ""
)