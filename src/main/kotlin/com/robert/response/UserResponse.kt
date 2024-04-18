package com.robert.response

import com.robert.domain.User

data class UserResponse(
    val id: String,
    val userName: String,
    val email: String,
    val password: String,
    val amountDue: String,
    val amountPaid: String
)

fun User.toResponse(): UserResponse {
    return UserResponse(
        id = id.toString(),
        userName = userName,
        email =  email,
        password = password,
        amountDue = amountDue,
        amountPaid = amountPaid
    )
}