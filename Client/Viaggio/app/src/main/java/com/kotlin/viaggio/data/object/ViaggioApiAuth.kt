package com.kotlin.viaggio.data.`object`

data class ViaggioApiAuth(val viaggioCustomToken: String, val docId: String)
data class SignUpBody(val name:String, val email: String, val password: String)
data class SignInBody(val email: String, val password: String)
