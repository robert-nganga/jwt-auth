package com.robert.domain.ports

import com.robert.domain.models.Transaction
import com.robert.domain.models.User

interface TransactionRepository {

    suspend fun insertTransaction(transaction: Transaction): String?

    suspend fun getTransactionsByUserId(id: String): List<Transaction>

    suspend fun getTransactionById(id: String): Transaction?
}