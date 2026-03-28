package com.appointmed.mobile.model

data class User(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "",
    val avatarUrl: String? = null,
    val avatarData: String? = null
)
