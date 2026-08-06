package com.civicconnect.data.dto.auth

import com.civicconnect.domain.model.User
import com.civicconnect.data.dto.admin.AdminAuthResponse

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = role,
    profilePicture = profilePicture,
    municipalArea = municipalArea
)

fun AuthResponse.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = role,
    municipalArea = municipalArea
)

fun AdminAuthResponse.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = role
)

fun ProfileUpdateResponse.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = role,
    municipalArea = municipalArea
)
