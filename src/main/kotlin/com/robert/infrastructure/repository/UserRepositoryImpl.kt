package com.robert.infrastructure.repository


import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.robert.domain.models.User
import com.robert.domain.ports.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class UserRepositoryImpl(
  private val mongoDb: MongoDatabase
) : UserRepository {

    companion object {
        const val USER_COLLECTION = "users"
    }

    private val collection = mongoDb.getCollection<User>(USER_COLLECTION)

    override suspend fun getUserById(id: String): User? {
        val objectId = ObjectId(id)
        return collection
            .find<User>(Filters.eq("_id", objectId))
            .firstOrNull()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return collection
            .find<User>(Filters.eq("email", email))
            .firstOrNull()
    }

    override suspend fun insertUser(user: User): String? {
        return collection
            .insertOne(user)
            .insertedId?.asObjectId()?.value?.toString()
    }

    override suspend fun updateUser(user: User): Boolean {
        val filter = Filters.eq("_id", user.id)

        val update = Updates.combine(
            set("amount", user.amount),
            set("due", user.due)
        )
        return collection.updateOne(filter, update).wasAcknowledged()

    }
}