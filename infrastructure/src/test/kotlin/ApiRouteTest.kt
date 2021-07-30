package com.github.vitormbgoncalves.starwarsmovies.infrastructure.test

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.vitormbgoncalves.restapi.JWTAuthHeader
import com.github.vitormbgoncalves.restapi.JWTAuthPayload
import com.github.vitormbgoncalves.restapi.jwtAuth
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.app.moduleWithDependencies
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.requestMovie
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.responseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas.responseMovie
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import com.google.common.io.Resources.getResource
import io.ktor.config.MapApplicationConfig
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
import redis.embedded.RedisServer
import java.time.Instant

/**
 * API routes tests
 *
 * @author Vitor Goncalves
 * @since 07.06.2021, seg, 09:50
 */

@Suppress("MaxLineLength")
object ApiRouteTest : Spek({

  /*
    JWK authentication provider mock
     */
  fun jwtAuthMock(): String {

    val privateKey = getResource("certs/jwt-private-key.pem").readText(Charsets.UTF_8)

    val secret = """(-----BEGIN PRIVATE KEY-----|-----END PRIVATE KEY-----)"""
      .toRegex().replace(privateKey, "")
      .trim()

    val header = JWTAuthHeader(
      "RS256",
      "JWT", "OZdxpBsB4bmypwduD8tbM8L9JFwo-f8oVzNok2KUBk4"
    )

    val payload = JWTAuthPayload(
      Instant.now().epochSecond + 3600,
      Instant.now().epochSecond,
      "http://localhost:8180/auth/realms/Ktor",
      "account"
    )

    return jwtAuth.createToken(secret, header, payload)
  }

  val jwksURL = getResource("certs/jwks.json").toURI().toString()

  val jwtToken = "Bearer ${jwtAuthMock()}"

  describe("API routes testing") {

    val engine = TestApplicationEngine()
    engine.start(wait = false)

    val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    val movieController = mockk<MovieController>(relaxed = true)

    val redis = RedisServer()

    beforeEachTest {
      clearMocks(movieController)
      engine.start(wait = false)
      engine.application.moduleWithDependencies(movieController)
    }

    beforeGroup {
      redis.start()
    }

    afterGroup {
      redis.stop()
    }

    with(engine) {
      (environment.config as MapApplicationConfig).apply {
        put("OAuth2.jwkIssuer", "http://localhost:8180/auth/realms/Ktor")
        put("OAuth2.jwksURL", jwksURL)
        put("OAuth2.jwkRealm", "ktor")
        put("OAuth2.audience", "account")
      }
    }

    with(engine) {
      it("should be OK when returning 200 status code") {
        with(
          handleRequest(HttpMethod.Get, "/openapi.json") {
            addHeader("Authorization", jwtToken)
          }
        ) {
          response.status() `should be` (HttpStatusCode.OK)
        }
      }

      it("should be OK when returning 301 status code") {
        with(
          handleRequest(HttpMethod.Get, "/") {
            addHeader("Authorization", jwtToken)
          }
        ) {
          response.status() `should be` (HttpStatusCode.MovedPermanently)
        }
      }

      it("should be OK when returning 200 status code") {

        coEvery { movieController.getMoviesWithPagination(1, 1) } returns responseAllMovies

        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies?page=1&size=1") {
            addHeader("Authorization", jwtToken)
          }
        ) {
          response.status() `should be` (HttpStatusCode.OK)
          coVerify { movieController.getMoviesWithPagination(1, 1) }
        }
      }

      it("should be OK when returning 200 status code") {

        coEvery { movieController.getMovie(any()) } returns responseMovie

        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies/60ac1ae25a74bf51382c469e") {
            addHeader("Authorization", jwtToken)
          }
        ) {
          response.status() `should be` (HttpStatusCode.OK)
          coVerify { movieController.getMovie("60ac1ae25a74bf51382c469e") }
        }
      }

      it("should be OK when returning 404 status code") {

        coEvery { movieController.getMovie(any()) } throws IllegalArgumentException("movie with the given id not found!")
        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies/60ac1ae25a74bf51382c569e") {
            addHeader("Authorization", jwtToken)
          }
        ) {
          response.status() `should be` (HttpStatusCode.NotFound)
          coVerify { movieController.getMovie("60ac1ae25a74bf51382c569e") }
        }
      }

      it("should be OK when throws error if the id is invalid") {

        coEvery { movieController.getMovie(any()) } throws IllegalArgumentException(
          "invalid hexadecimal representation of an ObjectId: [60bba776d0686920739c3cf]"
        )

        with(
          handleRequest(HttpMethod.Get, "/star-wars/movies/60bba776d0686920739c3cf") {
            addHeader("Authorization", jwtToken)
          }
        ) {
          response.content `should be equal to` "invalid hexadecimal representation of an ObjectId: [60bba776d0686920739c3cf]"
          coVerify { movieController.getMovie("60bba776d0686920739c3cf") }
        }
      }

      it("should be OK when returning 200 status code") {

        coEvery { movieController.updateMovie(any(), any()) } returns responseMovie

        with(
          handleRequest(HttpMethod.Put, "/star-wars/movies/60ac1ae25a74bf51382c469e") {
            addHeader("Authorization", jwtToken)
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
            addHeader("Authorization", jwtToken)
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
          handleRequest(HttpMethod.Delete, "/star-wars/movies/60ac1ae25a74bf51382c469e") {
            addHeader("Authorization", jwtToken)
          }
        ) {
          response.status() `should be` (HttpStatusCode.NoContent)
          coVerify { movieController.deleteMovie("60ac1ae25a74bf51382c469e") }
        }
      }
    }
  }
})
