package com.robert.domain.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: String,
    val email: String,
    val amount: Int = 0,
    val due: Int = 0,
    val password: String,
    val salt: String,
)
