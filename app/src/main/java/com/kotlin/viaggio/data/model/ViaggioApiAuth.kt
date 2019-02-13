package com.kotlin.viaggio.data.model

data class ViaggioApiAuth(val viaggioCustomToken: String, val docId: String)
data class SignUpBody(val userId: String, val password: String, val inviteCode: String)
data class SignInBody(val userId: String, val password: String)
enum class SignError {
    EMAIL_NOT_FOUND, WRONG_PW, DELETE_ID, EMAIL_MISMATCH,
    PW_MISMATCH, SAME_PW, INVALID_EMAIL_FORMAT, EXIST_EMAIL
}