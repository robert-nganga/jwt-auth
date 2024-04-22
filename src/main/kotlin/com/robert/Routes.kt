package com.robert

import com.robert.domain.models.Transaction
import com.robert.domain.models.User
import com.robert.domain.ports.TransactionRepository
import com.robert.domain.ports.UserRepository
import com.robert.request.AuthRequest
import com.robert.request.TransactionRequest
import com.robert.request.UserRequest
import com.robert.response.AuthResponse
import com.robert.response.ErrorResponse
import com.robert.response.toTransactionResponse
import com.robert.response.toUserResponse
import com.robert.security.hashing.HashingService
import com.robert.security.hashing.SaltedHash
import com.robert.security.tokens.TokenClaim
import com.robert.security.tokens.TokenConfig
import com.robert.security.tokens.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.codec.digest.DigestUtils
import org.bson.types.ObjectId

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserRepository,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signup") {
        val request = call.receiveNullable<UserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = "Invalid request"))
            return@post
        }

        val areFieldsBlank = request.email.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        if(areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict, ErrorResponse(message = "Invalid username or password"))
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            email = request.email,
            password = saltedHash.hash,
            salt = saltedHash.salt,
            name = request.name
        )

        // Verify if user already exists
        val dbUser = userDataSource.getUserByEmail(user.email)
        if(dbUser != null){
            call.respond(HttpStatusCode.Forbidden, ErrorResponse(message ="User with this email already exists"))
            return@post
        }

        val insertedId = userDataSource.insertUser(user)
        if(insertedId == null)  {
            call.respond(HttpStatusCode.Conflict, ErrorResponse(message ="Unknown Error occurred"))
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = insertedId
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.signIn(
    userDataSource: UserRepository,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveNullable<AuthRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(message ="Invalid request"))
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if(user == null) {
            call.respond(HttpStatusCode.Conflict, ErrorResponse(message = "Incorrect username or password"))
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if(!isValidPassword) {
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.password}")
            call.respond(HttpStatusCode.Conflict, ErrorResponse(message ="Incorrect username or password"))
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo(
    userDataSource: UserRepository,
) {
    authenticate {
        get("user") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if(userId == null){
                call.respond(HttpStatusCode.Conflict, ErrorResponse(message = "User not found"))
                return@get
            }
            println("got user id $userId")
            val user = userDataSource.getUserById(userId)
            println("User is $user")
            if(user == null){
                call.respond(HttpStatusCode.Conflict, ErrorResponse(message = "User not found"))
                return@get
            }
            call.respond(HttpStatusCode.OK, user.toUserResponse())
        }
    }
}

fun Route.insertTransaction(
    transactionRepository: TransactionRepository

){
    authenticate {
        post("insert-transaction") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if(userId == null){
                call.respond(HttpStatusCode.Conflict, ErrorResponse(message = "User not found"))
                return@post
            }

            val request = call.receiveNullable<TransactionRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(message ="Invalid request"))
                return@post
            }

            val areFieldsValid = request.amount <= 0 || request.type.isBlank()
            if(areFieldsValid) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse(message = "Invalid transaction details"))
                return@post
            }

            val transaction = Transaction(
                userId = ObjectId(userId),
                amount = request.amount,
                type = request.type
            )
            val insertedId = transactionRepository.insertTransaction(transaction)
            if(insertedId == null)  {
                call.respond(HttpStatusCode.Conflict, ErrorResponse(message ="Unknown Error occurred"))
                return@post
            }
            val dbTransaction = transactionRepository.getTransactionById(insertedId)
            if(dbTransaction == null)  {
                call.respond(HttpStatusCode.Conflict, ErrorResponse(message ="Unknown Error occurred"))
                return@post
            }
            call.respond(HttpStatusCode.OK, dbTransaction.toTransactionResponse())
        }
    }
}

fun Route.getUserTransactions(
    transactionRepository: TransactionRepository
){
    authenticate{
        get("transactions") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            if(userId == null){
                call.respond(HttpStatusCode.Conflict, ErrorResponse(message = "User not found"))
                return@get
            }
            val transactions = transactionRepository.getTransactionsByUserId(userId)
            println("my transactions are $transactions")
            call.respond(HttpStatusCode.OK, transactions.map { it.toTransactionResponse() })
        }
    }
}