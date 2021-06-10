package com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Serializer support for Date classes
 *
 * @author Vitor Goncalves
 * @since 09.06.2021, qua, 18:46
 */

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
object LocalDateSerialization : KSerializer<LocalDate> {
  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  override fun serialize(encoder: Encoder, value: LocalDate) {
    val string = dateFormatter.format(value)
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): LocalDate {
    val string = decoder.decodeString()
    return LocalDate.from(dateFormatter.parse(string))
  }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
object LocalDateTimeSerialization : KSerializer<LocalDateTime> {
  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

  override fun serialize(encoder: Encoder, value: LocalDateTime) {
    val string = dateFormatter.format(value)
    encoder.encodeString(string)
  }

  override fun deserialize(decoder: Decoder): LocalDateTime {
    val string = decoder.decodeString()
    return LocalDateTime.from(dateFormatter.parse(string))
  }
}

private val module = SerializersModule {
  contextual(LocalDateSerialization)
  contextual(LocalDateTimeSerialization)
}

val Json = Json { serializersModule = module }
