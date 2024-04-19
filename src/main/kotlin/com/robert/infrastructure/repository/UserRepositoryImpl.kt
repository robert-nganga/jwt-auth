package com.robert.infrastructure.repository


import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.robert.domain.models.User
import com.robert.domain.ports.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class UserRepositoryImpl(
  private val mongoDb: MongoDatabase
) : UserRepository {

    companion object{
        const val USER_COLLECTION = "users"
    }

    override suspend fun getUserById(id: String): User? {
        val objectId = ObjectId(id)
        return mongoDb.getCollection<User>(USER_COLLECTION)
            .find<User>(Filters.eq("_id", objectId))
            .firstOrNull()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return mongoDb.getCollection<User>(USER_COLLECTION)
            .find<User>(Filters.eq("email", email))
            .firstOrNull()
    }

    override suspend fun insertUser(user: User): String? {
        return mongoDb.getCollection<User>(USER_COLLECTION)
            .insertOne(user)
            .insertedId?.asObjectId()?.value?.toString()
    }
}