package com.civicconnect.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val profilePicture: String? = null,
    val municipalArea: String? = null
)
