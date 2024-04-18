package com.robert

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.robert.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}

fun createMongoDb(connectionString: String): MongoDatabase{
    return MongoClient.create(connectionString)
        .getDatabase("auth")
}
