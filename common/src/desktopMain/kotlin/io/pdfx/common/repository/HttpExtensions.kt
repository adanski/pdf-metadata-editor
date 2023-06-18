package io.pdfx.common.repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend inline fun HttpClient.getJson(
    url: String
): HttpResponse = get(url) { contentType(ContentType.Application.Json) }

suspend inline fun <reified T> HttpResponse.successfulBodyOrThrow(): T {
    if (status.value !in 200..299) {
        throw IllegalStateException("Unexpected response: $status")
    }

    return body<T>()
}
