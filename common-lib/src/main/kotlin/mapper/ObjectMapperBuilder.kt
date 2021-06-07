package com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.text.SimpleDateFormat

/**
 * Jackson serialization settings
 *
 * @author Vitor Goncalves
 * @since 24.05.2021, seg, 15:21
 */

object ObjectMapperBuilder {
  fun build(objectMapper: ObjectMapper = jacksonObjectMapper()): ObjectMapper {
    return objectMapper.apply {
      initializeBlock.invoke(this)
    }
  }

  private val initializeBlock: ObjectMapper.() -> Unit = {
    enable(
      SerializationFeature.WRAP_EXCEPTIONS,
      SerializationFeature.INDENT_OUTPUT
    )

    enable(
      DeserializationFeature.WRAP_EXCEPTIONS,
      DeserializationFeature.USE_BIG_INTEGER_FOR_INTS,
      DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS
    )

    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

    setSerializationInclusion(JsonInclude.Include.NON_NULL)

    setDefaultPrettyPrinter(
      DefaultPrettyPrinter().apply {
        indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
        indentObjectsWith(DefaultIndenter("  ", "\n"))
      }
    )

    registerModule(KotlinModule())
    registerModule(JavaTimeModule())

    dateFormat = SimpleDateFormat("dd-MM-yyyy")
  }
}
