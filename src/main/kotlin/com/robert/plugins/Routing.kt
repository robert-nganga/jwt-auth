package com.robert.plugins

import com.robert.*
import com.robert.domain.ports.TransactionRepository
import com.robert.domain.ports.UserRepository
import com.robert.security.hashing.HashingService
import com.robert.security.tokens.TokenConfig
import com.robert.security.tokens.TokenService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    transactionRepository: TransactionRepository
) {
    routing {
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        signUp(hashingService, userDataSource, tokenService, tokenConfig)
        authenticate()
        getSecretInfo(userDataSource)
        insertTransaction(transactionRepository)
        getUserTransactions(transactionRepository)
    }
}
