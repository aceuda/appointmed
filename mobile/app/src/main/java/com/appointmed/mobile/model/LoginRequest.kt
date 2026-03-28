package com.appointmed.mobile.model

data class LoginRequest(
    val email: String,
    val password: String,
    val role: String
)
