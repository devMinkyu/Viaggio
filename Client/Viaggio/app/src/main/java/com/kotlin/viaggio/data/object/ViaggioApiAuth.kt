package com.kotlin.viaggio.data.`object`

data class ViaggioApiAuth(val viaggioCustomToken: String, val docId: String)
data class SignUpBody(val name:String, val email: String, val password: String)
data class SignInBody(val email: String, val password: String)
enum class SignError {
    EMAIL_NOT_FOUND, WRONG_PW, DELETE_ID, EMAIL_MISMATCH,
    PW_MISMATCH, SAME_PW, INVALID_EMAIL_FORMAT, EXIST_EMAIL
}