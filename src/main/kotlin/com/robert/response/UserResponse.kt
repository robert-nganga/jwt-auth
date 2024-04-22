package com.robert.response

import com.robert.domain.models.User
import kotlinx.serialization.Serializable


@Serializable
data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val amount: Int,
    val due: Int
)

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = id.toString(),
        email = email,
        name = name,
        amount = amount,
        due = due
    )
}
