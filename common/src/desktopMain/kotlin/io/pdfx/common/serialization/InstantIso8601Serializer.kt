package io.pdfx.common.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.Instant

/**
 * A `kotlinx-datetime` serializer for [Instant] that uses the ISO-8601 representation.
 *
 * JSON example: `"2020-12-09T09:16:56.000124Z"`
 *
 * @see Instant.toString
 * @see Instant.parse
 */
object InstantIso8601Serializer: KSerializer<Instant> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant =
        Instant.parse(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

}
