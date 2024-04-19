package com.robert

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.robert.domain.models.User
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
    val secret = System.getenv("JWT_SECRET")
    val db = createMongoDb(
        "mongodb+srv://admin:$pass@cluster0.wqakbcd.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0",
        "Cluster0"
    )
    val userDataSource = UserRepositoryImpl(db)
//    GlobalScope.launch {
//        val user = User(
//            email = "test1@gmail.com",
//            password = "testpassword",
//            salt = "salt"
//        )
//        val inserted = userDataSource.insertUser(user)
//        val dbUser = userDataSource.getUserByEmail(user.email)
//        println("********************************************:: $inserted")
//        println("********************************************:: $dbUser")
//    }
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = "secret"
    )
    val hashingService = SHA256HashingService()
    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}

fun createMongoDb(connectionString: String, databaseName: String): MongoDatabase{
    return MongoClient.create(connectionString)
        .getDatabase(databaseName)
}
