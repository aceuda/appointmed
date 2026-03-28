package com.appointmed.mobile.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String,
    val address: String? = null,
    val gender: String? = null,
    val birthDate: String? = null,
    val specialization: String? = null,
    val licenseNumber: String? = null,
    val phone: String? = null,
    val clinicAddress: String? = null
)
