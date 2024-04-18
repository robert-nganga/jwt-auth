package com.robert.domain

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userName: String,
    val email: String,
    val password: String,
    val amountDue: String,
    val amountPaid: String,
)
