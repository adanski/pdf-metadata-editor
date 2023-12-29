package io.pdfx.common.repository.release

import io.ktor.client.*
import io.pdfx.common.repository.HTTP_CLIENT
import io.pdfx.common.repository.successfulBodyOrThrow
import io.pdfx.common.repository.getJson

private const val LATEST_RELEASE_URL = "https://api.github.com/repos/adanski/pdfx-metadata-editor/releases/latest"

actual object ReleaseRepository {
    private val httpClient: HttpClient = HTTP_CLIENT

    actual suspend fun findLatestRelease(): AppRelease {
        return httpClient.getJson(LATEST_RELEASE_URL).successfulBodyOrThrow()
    }
}
