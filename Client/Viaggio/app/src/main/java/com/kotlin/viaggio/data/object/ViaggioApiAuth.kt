package com.kotlin.viaggio.data.`object`

data class ViaggioApiAuth( val email: String, val name: String, val token: String)
data class SignUpBody(val name:String, val email: String, val passwordHash: String, val passwordHash2: String)
data class SignInBody(val email: String, val password: String)
