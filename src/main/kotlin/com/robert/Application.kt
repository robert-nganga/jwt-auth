package com.robert

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.robert.domain.models.User
import com.robert.infrastructure.repository.TransactionRepositoryImpl
import com.robert.infrastructure.repository.UserRepositoryImpl
import com.robert.plugins.*
import com.robert.security.hashing.SHA256HashingService
import com.robert.security.tokens.JwtTokenService
import com.robert.security.tokens.TokenConfig
import io.ktor.server.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val pass = System.getenv("MONGO_PW")
    //val secret = System.getenv("JWT_SECRET")
    val mongoClient = MongoClient.create(
        "mongodb+srv://admin:$pass@cluster0.wqakbcd.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
    )
    val mongoDatabase = mongoClient.getDatabase("Cluster0")
    val userDataSource = UserRepositoryImpl(mongoDatabase)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 7L * 1000L * 60L * 60L * 24L,
        secret = "my-secret"
    )
    val hashingService = SHA256HashingService()
    val transactionRepository = TransactionRepositoryImpl(mongoClient)
    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig, transactionRepository)
}

fun createMongoDb(connectionString: String, databaseName: String): MongoDatabase{
    return MongoClient.create(connectionString)
        .getDatabase(databaseName)
}
