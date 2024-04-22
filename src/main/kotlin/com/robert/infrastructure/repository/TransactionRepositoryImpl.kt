package com.robert.infrastructure.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.InsertOneModel
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.robert.domain.models.Transaction
import com.robert.domain.ports.TransactionRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId

class TransactionRepositoryImpl(
    private val mongoClient: MongoClient
): TransactionRepository {

    companion object {
        const val TRANSACTION_COLLECTION = "transactions"
        const val MONGO_DATABASE = "Cluster0"
    }

    private val mongoDatabase = mongoClient.getDatabase(MONGO_DATABASE)
    private val transactionCollection = mongoDatabase.getCollection<Transaction>(TRANSACTION_COLLECTION)

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