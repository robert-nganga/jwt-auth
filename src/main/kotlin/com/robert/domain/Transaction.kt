package com.robert.domain

import org.bson.types.ObjectId

data class Transaction(
    val id: ObjectId,
    val amount: String
)
