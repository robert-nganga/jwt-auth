package com.robert.request

import kotlinx.serialization.Serializable


@Serializable
data class TransactionRequest(
    val amount: Int,
    val type: String
)
