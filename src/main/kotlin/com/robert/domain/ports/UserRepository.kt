package com.robert.domain.ports

import com.robert.domain.models.User

interface UserRepository {
    suspend fun getUserByEmail(email: String): User?
    suspend fun insertUser(user: User): Boolean
}