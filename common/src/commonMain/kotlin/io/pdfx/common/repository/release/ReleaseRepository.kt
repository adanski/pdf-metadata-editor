package io.pdfx.common.repository.release

expect object ReleaseRepository {
    suspend fun findLatestRelease(): AppRelease
}
