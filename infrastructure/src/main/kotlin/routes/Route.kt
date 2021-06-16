package com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes

import cn.zenliu.ktor.redis.RedisFactory
import com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper.Json
import com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper.ObjectMapperBuilder
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.PageQuery
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.StringParam
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.Tag
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.jwtAuth
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.requestMovie
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.responseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.responseMovie
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
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.lettuce.core.codec.StringCodec
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.slf4j.LoggerFactory

/**
 * Ktor routing controller
 *
 * @author Vitor Goncalves
 * @since 18.05.2021, ter, 11:02
 */

@OptIn(ExperimentalStdlibApi::class)
fun Routing.route(movieController: MovieController) {

  val client = RedisFactory.newReactiveClient(StringCodec.UTF8)

  val logger = LoggerFactory.getLogger("OpenAPI Route")

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

    jwtAuth {

      route("star-wars/movies") {

        tag(Tag.`Star Wars films`) {
          throws(
            HttpStatusCode.NotFound,
            Exception().localizedMessage,
            { ex: Exception -> ex.message }
          ) {

            status(200) {
              get<PageQuery, ResponseAllMovies, JWTPrincipal>(
                info(
                  summary = "Find all movies.",
                  description = "Find all movies with pagination."
                ),
                example = responseAllMovies
              ) { (page, size) ->
                val movies = movieController.getMoviesPage(page, size)
                val result = client.get("page=$page&size=$size").awaitSingleOrNull()

                result?.let {
                  respond(Json.decodeFromString(it))
                  logger.trace("Data from Redis")
                } ?: run {
                  client.set("page=$page&size=$size", Json.encodeToString(movies)).subscribe()
                  client.expire("page=$page&size=$size", 20).subscribe()
                  logger.trace("Data from MongoDB")
                  respond(movies)
                }
              }

              get<StringParam, ResponseMovieDTO, JWTPrincipal>(
                info(
                  summary = "Find movie.",
                  description = "Find movie by id."
                ),
                example = responseMovie
              ) { (id) ->
                val movie = movieController.getMovie(id) ?: return@get
                val result = client.get(id).awaitSingleOrNull()
                result?.let {
                  respond(Json.decodeFromString(it))
                  logger.trace("Data from Redis")
                } ?: run {
                  client.set(id, Json.encodeToString(movie)).subscribe()
                  client.expire(id, 50).subscribe()
                  logger.trace("Data from MongoDB")
                  respond(movie)
                }
              }

              put<StringParam, ResponseMovieDTO, RequestMovieDTO, JWTPrincipal>(
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
              post<Unit, ResponseMovieDTO, RequestMovieDTO, JWTPrincipal>(
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
              delete<StringParam, Unit, JWTPrincipal>(
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
