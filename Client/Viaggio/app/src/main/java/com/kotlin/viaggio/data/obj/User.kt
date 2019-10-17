package com.kotlin.viaggio.data.obj

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) var id:Int,
    var name:String = "",
    var email:String ="",
    var profileImageName:String = ""
)
data class GoogleSignInBody(val id_token: String)