package com.robert.domain.models

import org.bson.types.ObjectId

data class Transaction(
    val id: ObjectId,
    val amount: String
)
