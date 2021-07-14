package com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes

import cn.zenliu.ktor.redis.RedisFactory
import com.github.fstien.kotlin.logging.opentracing.decorator.withOpenTracingLogs
import com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper.Json
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.OAuth
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.PageQuery
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.StringParam
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.Tag
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.requestMovie
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.responseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.responseMovie
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.tracing.OpenTracing
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.RequestMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseMovieDTO
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.auth.delete
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.put
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.status
import com.papsign.ktor.openapigen.route.tag
import com.papsign.ktor.openapigen.route.throws
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.codec.StringCodec
import io.opentracing.contrib.redis.lettuce52.TracingStatefulRedisConnection
import kotlinx.coroutines.future.await
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging

/**
 * Ktor routing controller
 *
 * @author Vitor Goncalves
 * @since 18.05.2021, ter, 11:02
 */

@OptIn(ExperimentalStdlibApi::class)
fun Routing.route(movieController: MovieController) {

  val client = RedisFactory.newAsyncClient(StringCodec.UTF8)

  val connection: StatefulRedisConnection<String, String> =
    TracingStatefulRedisConnection(client.statefulConnection, OpenTracing.tracingConfiguration)

  val commands: RedisAsyncCommands<String, String> = connection.async()

  val logger = KotlinLogging.logger {}.withOpenTracingLogs()

  get("/openapi.json") {

    /*val mapper = ObjectMapperBuilder.build()

    val json = mapper.writeValueAsString(application.openAPIGen.api.serialize()).replace(
      oldValue = """"securitySchemes":{"jwtAuth":{"bearerFormat":"JWT","name":"jwtAuth","scheme":"bearer","type":"http"}}""",
      newValue = """"securitySchemes":{"jwtAuth":{"bearerFormat":"JWT","scheme":"bearer","type":"http"}}""",
      ignoreCase = false
    ).replace(
      """"security":[{}]""",
      """"security":[{"jwtAuth":[]}]""",
      false
    )*/

    call.respond(application.openAPIGen.api.serialize())
  }
  get("/") {
    call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
  }

  apiRouting {

    OAuth {

      route("star-wars/movies") {

        tag(Tag.`Star Wars films`) {
          throws(
            HttpStatusCode.NotFound,
            Exception().localizedMessage,
            { ex: Exception -> ex.message }
          ) {

            status(200) {
              get<PageQuery, ResponseAllMovies, OAuthAccessTokenResponse>(
                info(
                  summary = "Find all movies.",
                  description = "Find all movies with pagination."
                ),
                example = responseAllMovies
              ) { (page, size) ->
                val result = commands.get("page=$page&size=$size").await()
                result?.let {
                  respond(Json.decodeFromString(it))
                  logger.trace("Data from Redis")
                } ?: run {
                  val movies = movieController.getMoviesPage(page, size)
                  commands.set("page=$page&size=$size", Json.encodeToString(movies))
                  commands.expire("page=$page&size=$size", 20)
                  logger.trace("Data from MongoDB")
                  respond(movies)
                }
              }

              get<StringParam, ResponseMovieDTO, OAuthAccessTokenResponse>(
                info(
                  summary = "Find movie.",
                  description = "Find movie by id."
                ),
                example = responseMovie
              ) { (id) ->
                val result = commands.get(id).await()
                result?.let {
                  respond(Json.decodeFromString(it))
                  logger.trace("Data from Redis")
                } ?: run {
                  val movie = movieController.getMovie(id) ?: return@get
                  commands.set(id, Json.encodeToString(movie))
                  commands.expire(id, 50)
                  logger.trace("Data from MongoDB")
                  respond(movie)
                }
              }

              put<StringParam, ResponseMovieDTO, RequestMovieDTO, OAuthAccessTokenResponse>(
                info(
                  summary = "Update movie.",
                  description = "Update movie by id."
                ),
                exampleRequest = requestMovie,
                exampleResponse = responseMovie
              ) { (id), body ->
                respond(movieController.updateMovie(id, body) ?: return@put)
              }
            }

            status(201) {
              post<Unit, ResponseMovieDTO, RequestMovieDTO, OAuthAccessTokenResponse>(
                info(
                  summary = "Register movie.",
                  description = "Register new movie."
                ),
                exampleRequest = requestMovie,
                exampleResponse = responseMovie
              ) { _, body ->
                respond(movieController.createMovie(body))
              }
            }

            status(204) {
              delete<StringParam, Unit, OAuthAccessTokenResponse>(
                info(
                  summary = "Delete movie.",
                  description = "Delete movie by id."
                )
              ) { (id) ->
                respond(movieController.deleteMovie(id))
              }
            }
          }
        }
      }
    }
  }
}
