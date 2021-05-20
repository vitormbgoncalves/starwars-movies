package com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes

import com.github.vitormbgoncalves.starwarsmovies.interfaces.MovieController
import com.github.vitormbgoncalves.starwarsmovies.interfaces.NewMovieDTO
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import org.koin.ktor.ext.inject

/**
 * Ktor routing controller
 *
 * @author Vitor Goncalves
 * @since 18.05.2021, ter, 11:02
 */

fun Routing.route() {

  val movieController: MovieController by inject()

  route("/star-wars") {
    get("/movies") {
      val movies = movieController.getAllMovies()
      call.respond(movies)
    }
    get("/movies/{id}") {
      val id: String = call.parameters["id"] ?: return@get
      val movie = movieController.getMovie(id)
      if (movie != null) {
        call.respond(movie)
      } else {
        call.respondText("Movie with this id not found!", ContentType.Text.Plain, HttpStatusCode.NotFound)
      }
    }
    post("/movies") {
      val movie = call.receive<NewMovieDTO>()
      if (movieController.createMovie(movie)) {
        call.respond(HttpStatusCode.Created)
      } else {
        call.respondText("Movie not registered!", ContentType.Text.Plain, HttpStatusCode.NotFound)
      }
    }
    put("/movies/{id}") {
      val id = call.parameters["id"] ?: return@put
      val movie = call.receive<NewMovieDTO>()
      if (movieController.updateMovie(id, movie)) {
        call.respond(HttpStatusCode.NoContent)
      } else {
        call.respondText("Movie with this id not found!", ContentType.Text.Plain, HttpStatusCode.NotFound)
      }
    }
    delete("/movies/{id}") {
      val id = call.parameters["id"] ?: return@delete
      if (movieController.deleteMovie(id)) {
        call.respond(HttpStatusCode.NoContent)
      } else {
        call.respondText("Movie with this id not found!", ContentType.Text.Plain, HttpStatusCode.NotFound)
      }
    }
  }
}
