package com.github.vitormbgoncalves.starwarsmovies.infrastructure

import com.fasterxml.jackson.databind.SerializationFeature
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.module.KoinModuleBuilder
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes.route
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respondText
import io.ktor.routing.routing
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level

/**
 * Ktor main file
 *
 * @author Vitor Goncalves
 * @since 18.05.2021, ter, 20:23
 */

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.main(testing: Boolean = false) {

  install(ContentNegotiation) {
    jackson {
      enable(SerializationFeature.INDENT_OUTPUT)
    }
  }

  install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
  }

  install(Koin) {
    modules(KoinModuleBuilder)
  }

  install(DefaultHeaders)

  install(StatusPages) {
    this.exception<Throwable> { e ->
      call.respondText(e.localizedMessage, ContentType.Text.Plain)
      throw e
    }
  }

  routing {
    route()
  }
}
