package com.robert.domain.ports

import com.robert.domain.models.User

interface UserRepository {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun insertUser(user: User): String?
}