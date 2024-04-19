package com.robert.plugins

import com.robert.authenticate
import com.robert.domain.ports.UserRepository
import com.robert.getSecretInfo
import com.robert.security.hashing.HashingService
import com.robert.security.tokens.TokenConfig
import com.robert.security.tokens.TokenService
import com.robert.signIn
import com.robert.signUp
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        signUp(hashingService, userDataSource, tokenService, tokenConfig)
        authenticate()
        getSecretInfo()
    }
}
