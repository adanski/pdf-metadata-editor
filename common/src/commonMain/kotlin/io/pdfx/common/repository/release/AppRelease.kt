package io.pdfx.common.repository.release

import java.time.Instant

expect class AppRelease {
    val date: Instant
    val version: String
    val url: String
}
