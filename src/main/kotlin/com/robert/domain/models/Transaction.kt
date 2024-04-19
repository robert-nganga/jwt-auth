package com.robert.domain.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Transaction(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val amount: Int,
    val date: Long = System.currentTimeMillis(),
    val type: String
)
