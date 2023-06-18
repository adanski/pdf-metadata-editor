package io.pdfx.common.serialization

import kotlinx.serialization.Serializable

typealias Instant = @Serializable(with=InstantIso8601Serializer::class) java.time.Instant
