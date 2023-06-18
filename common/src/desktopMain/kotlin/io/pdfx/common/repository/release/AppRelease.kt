package io.pdfx.common.repository.release

import io.pdfx.common.serialization.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
actual data class AppRelease(
    @SerialName("published_at")
    actual val date: Instant,
    @SerialName("tag_name")
    actual val version: String,
    @SerialName("html_url")
    actual val url: String
)
