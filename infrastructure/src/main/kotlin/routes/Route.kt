package com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes

import com.github.vitormbgoncalves.starwarsmovies.infrastructure.openAPIGeneratorConfig.PageQuery
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.openAPIGeneratorConfig.StringParam
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.openAPIGeneratorConfig.Tag
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.RequestMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseMovieDTO
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.path.normal.put
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.status
import com.papsign.ktor.openapigen.route.tag
import com.papsign.ktor.openapigen.route.throws
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.koin.ktor.ext.inject

/**
 * Ktor routing controller
 *
 * @author Vitor Goncalves
 * @since 18.05.2021, ter, 11:02
 */

fun Routing.route() {

  val movieController: MovieController by inject()

  get("/openapi.json") {
    call.respond(application.openAPIGen.api.serialize())
  }
  get("/") {
    call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
  }

  apiRouting {
    route("star-wars/movies") {
      tag(Tag.`Star Wars films`) {
        throws(
          HttpStatusCode.NotFound,
          Exception().localizedMessage,
          { ex: Exception -> ex.message }
        ) {

          status(200) {
            get<PageQuery, ResponseAllMovies>(
              info(
                summary = "Find all movies.",
                description = "Find all movies with pagination."
              ),
              example = responseAllMovies
            ) { (page, size) ->

              respond(movieController.getMoviesPage(page, size))
            }

            get<StringParam, ResponseMovieDTO>(
              info(
                summary = "Find movie.",
                description = "Find movie by id."
              ),
              example = responseMovie
            ) { (id) ->
              respond(movieController.getMovie(id) ?: return@get)
            }

            put<StringParam, ResponseMovieDTO, RequestMovieDTO>(
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
            post<Unit, ResponseMovieDTO, RequestMovieDTO>(
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
            delete<StringParam, Unit>(
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
