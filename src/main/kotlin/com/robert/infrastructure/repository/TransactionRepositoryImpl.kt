package com.robert.infrastructure.repository

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.robert.domain.models.Transaction
import com.robert.domain.ports.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

class TransactionRepositoryImpl(
    mongoDb: MongoDatabase
): TransactionRepository {

    companion object {
        const val TRANSACTION_COLLECTION = "transactions"
    }

    private val transactionCollection = mongoDb.getCollection<Transaction>(TRANSACTION_COLLECTION)

    override suspend fun insertTransaction(transaction: Transaction): String? {
        return transactionCollection
            .insertOne(transaction)
            .insertedId?.asObjectId()?.value?.toString()
    }

    override suspend fun getTransactionsByUserId(id: String): List<Transaction> {
        val objectId = ObjectId(id)
        return transactionCollection
            .find<Transaction>(Filters.eq("userId",objectId ))
            .toList()
    }

    override suspend fun getTransactionById(id: String): Transaction? {
        val objectId = ObjectId(id)
        return  transactionCollection
            .find(Filters.eq("_id", objectId))
            .firstOrNull()
    }

}