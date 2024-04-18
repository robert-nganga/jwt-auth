package com.robert.response

import kotlinx.serialization.Serializable


@Serializable
data class AuthResponse(
    val token: String
)
