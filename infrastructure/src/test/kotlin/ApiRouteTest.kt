package com.github.vitormbgoncalves.starwarsmovies.infrastructure.test

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.moduleWithDependencies
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes.requestMovie
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes.responseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes.responseMovie
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * API routes tests
 *
 * @author Vitor Goncalves
 * @since 07.06.2021, seg, 09:50
 */

object ApiRouteTest : Spek({

  describe("API routes testing") {

    val engine = TestApplicationEngine()
    engine.start(wait = false)

    val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    val movieController = mockk<MovieController>(relaxed = true)

    beforeEachTest {
      clearMocks(movieController)
      engine.start(wait = false)
      engine.application.moduleWithDependencies(movieController)
    }

    with(engine) {
      it("should be OK when returning 200 status code") {
        with(
          handleRequest(HttpMethod.Get, "/openapi.json")
        ) {
          response.status() `should be` (HttpStatusCode.OK)
        }
      }

      it("should be OK when returning 301 status code") {
        with(
          handleRequest(HttpMethod.Get, "/")
        ) {
          response.status() `should be` (HttpStatusCode.MovedPermanently)
        }
      }

      it("should be OK when returning 200 status code") {

        coEvery { movieController.getMoviesPage(1, 1) } returns responseAllMovies

        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies?page=1&size=1")
        ) {
          response.status() `should be` (HttpStatusCode.OK)
          coVerify { movieController.getMoviesPage(1, 1) }
        }
      }

      it("should be OK when returning 200 status code") {

        coEvery { movieController.getMovie(any()) } returns responseMovie

        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies/60ac1ae25a74bf51382c469e")
        ) {
          response.status() `should be` (HttpStatusCode.OK)
          coVerify { movieController.getMovie("60ac1ae25a74bf51382c469e") }
        }
      }

      it("should be OK when returning 404 status code") {

        coEvery { movieController.getMovie(any()) } returns null

        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies/60ac1ae25a74bf51382c469e")
        ) {
          response.status() `should be` (HttpStatusCode.NotFound)
          coVerify { movieController.getMovie("60ac1ae25a74bf51382c469e") }
        }
      }

      it("should be OK when throws error if the id is invalid") {

        coEvery { movieController.getMovie(any()) } throws IllegalArgumentException(
          "invalid hexadecimal representation of an ObjectId: [60bba776d0686920739c3cf]"
        )

        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies/60bba776d0686920739c3cf")
        ) {
          response.content `should be equal to` "invalid hexadecimal representation of an ObjectId: [60bba776d0686920739c3cf]"
          coVerify { movieController.getMovie("60bba776d0686920739c3cf") }
        }
      }

      it("should be OK when returning 200 status code") {

        coEvery { movieController.updateMovie(any(), any()) } returns responseMovie

        with(
          handleRequest(HttpMethod.Put, "/star-wars/movies/60ac1ae25a74bf51382c469e") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(mapper.writeValueAsString(requestMovie))
          }
        ) {
          response.status() `should be` (HttpStatusCode.OK)
          coVerify { movieController.updateMovie("60ac1ae25a74bf51382c469e", requestMovie) }
        }
      }

      it("should be OK when returning 201 status code") {

        coEvery { movieController.createMovie(any()) } returns responseMovie

        with(
          handleRequest(HttpMethod.Post, "/star-wars/movies") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(mapper.writeValueAsString(requestMovie))
          }
        ) {
          response.status() `should be` (HttpStatusCode.Created)
          coVerify { movieController.createMovie(requestMovie) }
        }
      }

      it("should be OK when returning 204 status code") {

        coEvery { movieController.deleteMovie(any()) } returns Unit

        with(
          handleRequest(HttpMethod.Delete, "/star-wars/movies/60ac1ae25a74bf51382c469e")
        ) {
          response.status() `should be` (HttpStatusCode.NoContent)
          coVerify { movieController.deleteMovie("60ac1ae25a74bf51382c469e") }
        }
      }
    }
  }
})
