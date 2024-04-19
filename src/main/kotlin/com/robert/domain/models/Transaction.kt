package com.robert.domain.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Transaction(
    @BsonId
    val id: ObjectId,
    val userId: ObjectId,
    val amount: String,
    val date: String,
    val type: String
)
