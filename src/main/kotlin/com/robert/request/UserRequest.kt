package com.robert.request

import com.robert.domain.User
import org.bson.types.ObjectId

data class UserRequest(
    val userName: String,
    val email: String,
    val password: String,
    val amountDue: String,
    val amountPaid: String
)

fun UserRequest.toDomain(): User {
    return User(
        id = ObjectId(),
        userName = userName,
        email = email,
        password = password,
        amountDue = amountDue,
        amountPaid = amountPaid
    )
}
