package com.github.vitormbgoncalves.starwarsmovies.infrastructure.app

import cn.zenliu.ktor.redis.RedisFactory
import com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper.ObjectMapperBuilder
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.health.MongoHealthCheck
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.health.RedisHealthCheck
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.module.KoinModuleBuilder
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.installAuth
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.installOpenApi
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes.route
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import com.typesafe.config.ConfigFactory
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
import ktor_health_check.Health
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.SLF4JLogger
import org.slf4j.event.Level

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

  installAuth()

  installOpenApi()

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
    url = ConfigFactory.load("redis.conf").getString("REDIS_URL")
  }

  install(Health) {
    readyCheck("MongoDB") { MongoHealthCheck.check() }
    readyCheck("Redis") { RedisHealthCheck.check() }
  }

  routing {
    trace { application.log.trace(it.buildText()) }
    route(movieController)
  }
}
