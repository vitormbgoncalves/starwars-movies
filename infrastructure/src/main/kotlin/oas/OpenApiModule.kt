package com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.ktor.application.Application
import io.ktor.application.install
import kotlin.reflect.KType

/**
 * OpenAPI Generator configuration
 *
 * @author Vitor Goncalves
 * @since 12.06.2021, sáb, 19:55
 */

fun Application.installOpenApi() {

  install(OpenAPIGen) {
    info {
      version = "1.0-SNAPSHOT"
      title = "Star Wars Movies Catalog"
      description = "Full list of Star Wars films"
      contact {
        name = "Vitor Goncalves"
        url = "https://vitorgoncalves.me"
      }
    }
    replaceModule(
      DefaultSchemaNamer,
      object : SchemaNamer {
        val regex = Regex("[A-Za-z0-9_.]+")
        override fun get(type: KType): String {
          return type.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
        }
      }
    )
  }
}
