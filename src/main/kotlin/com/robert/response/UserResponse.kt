package com.robert.response

import com.robert.domain.models.User
import kotlinx.serialization.Serializable


@Serializable
data class UserResponse(
    val id: String,
    val email: String
)

fun User.toUserResponse(): UserResponse{
    return UserResponse(
        id = id.toString(),
        email = email
    )
}
