package com.robert.response

import com.robert.domain.models.Transaction
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class TransactionResponse(
    val id: String,
    val userId: String,
    val amount: Int,
    val date: Long,
    val type: String
)


fun Transaction.toTransactionResponse(): TransactionResponse {
    return TransactionResponse(
        id =  id.toString(),
        userId = userId.toString(),
        amount = amount,
        date = date,
        type = type
    )
}
