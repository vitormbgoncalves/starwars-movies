package com.github.vitormbgoncalves.starwarsmovies.infrastructure.app

import cn.zenliu.ktor.redis.RedisFactory
import com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper.ObjectMapperBuilder
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.module.KoinModuleBuilder
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes.route
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.request.path
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.SLF4JLogger
import org.slf4j.event.Level
import kotlin.reflect.KType

/**
 * Ktor main file
 *
 * @author Vitor Goncalves
 * @since 18.05.2021, ter, 20:23
 */

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.main() {

  install(Koin) {
    SLF4JLogger()
    modules(
      KoinModuleBuilder
    )
  }

  val movieController: MovieController by inject()

  moduleWithDependencies(movieController)
}

fun Application.moduleWithDependencies(movieController: MovieController) {

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

  install(ContentNegotiation) {
    jackson {
      ObjectMapperBuilder.build(this)
    }
  }

  install(DefaultHeaders)

  install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
  }

  install(StatusPages) {
    this.exception<Throwable> { e ->
      call.respondText(e.localizedMessage, ContentType.Text.Plain)
      throw e
    }
  }

  install(RedisFactory) {
    url = environment.config.property("redis.url").getString()
  }

  routing {
    trace { application.log.trace(it.buildText()) }
    route(movieController)
  }
}
