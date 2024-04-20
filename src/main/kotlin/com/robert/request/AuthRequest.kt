package com.robert.request

import kotlinx.serialization.Serializable


@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val name: String
)
