package io.pdfx.common.repository

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import java.net.http.HttpClient.Version.HTTP_2

val HTTP_CLIENT: HttpClient by lazy(LazyThreadSafetyMode.PUBLICATION) {
    HttpClient(Java) {
        engine {
            threadsCount = 4
            pipelining = true
            protocolVersion = HTTP_2
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }
}
