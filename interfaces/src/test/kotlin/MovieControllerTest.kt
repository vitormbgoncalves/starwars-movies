package com.github.vitormbgoncalves.starwarsmovies.interfaces.testing

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.service.MovieServiceImpl
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.HalLink
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.RequestMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseMovieDTO
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContain
import org.litote.kmongo.toId
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * Movie controller test
 *
 * @author Vitor Goncalves
 * @since 02.06.2021, qua, 10:05
 */

object MovieControllerTest : Spek({

  val mapper =
    jacksonObjectMapper().registerModule(JavaTimeModule()).setSerializationInclusion(JsonInclude.Include.NON_NULL)

  describe("Request movies") {

    val mockMovieService = mockk<MovieServiceImpl>(relaxed = true)

    val movieController = MovieController(mockMovieService)

    beforeEachTest {
      clearMocks(mockMovieService)
    }

    it("Get all movies") {
      runBlocking {
        coEvery { mockMovieService.findAll(any(), any()) } returns listOf(movie)
        val movies = mapper.writeValueAsString(movieController.getAllMovies(1, 1))
        movies shouldContain movieJson
        coVerify { mockMovieService.findAll(1, 1) }
      }
    }

    it("Get movie by id") {
      runBlocking {
        coEvery { mockMovieService.findById(any()) } returns movie
        val movie = mapper.writeValueAsString(movieController.getMovie("60afdbe0b3c7c176f1f7988c"))
        movie shouldBeEqualTo movieJson
        coVerify { mockMovieService.findById("60afdbe0b3c7c176f1f7988c") }
      }
    }
    it("Creat movie") {
      runBlocking {
        coEvery { mockMovieService.create(any()) } returns movie
        val movie = mapper.writeValueAsString(movieController.createMovie(requestMovie))
        movie shouldBeEqualTo movieJson
        coVerify { mockMovieService.create(any()) }
      }
    }
    it("Update movie") {
      runBlocking {
        coEvery { mockMovieService.update(any(), any()) } returns movie
        val movie = mapper.writeValueAsString(movieController.updateMovie("60afdbe0b3c7c176f1f7988c", requestMovie))
        movie shouldBeEqualTo movieJson
        coVerify { mockMovieService.update(any(), any()) }
      }
    }
    it("Delete movie") {
      runBlocking {
        coEvery { mockMovieService.delete(any()) } returns Unit
        movieController.deleteMovie("60afdbe0b3c7c176f1f7988c")
        coVerify { mockMovieService.delete("60afdbe0b3c7c176f1f7988c") }
      }
    }
    it("Get movies with pagination") {
      runBlocking {
        coEvery { mockMovieService.findAll(any(), any()) } returns listOf(movie)
        coEvery { mockMovieService.totalMovies() } returns 1
        movieController.getMoviesPage(1, 1) shouldBeInstanceOf ResponseAllMovies::class
        coVerify { mockMovieService.findAll(0, 1) }
        coVerify { mockMovieService.totalMovies() }
      }
    }
  }
})

private val movie = Movie(
  "60ac1ae25a74bf51382c469e".toId(),
  "A New Hope",
  4,
  "Princess Leia is captured...",
  Series.SKYWALKER_SAGA,
  Trilogy.ORIGINAL,
  LocalDate.of(1977, 5, 25),
  "George Lucas",
  listOf("George Lucas"),
  listOf("George Lucas"),
  listOf("Gary Kurtz"),
  8.6,
  LocalDateTime.of(2021, Month.JUNE, 2, 6, 30, 40, 50000),
  LocalDateTime.of(2021, Month.JUNE, 2, 6, 30, 40, 50000)
)

private val requestMovie = RequestMovieDTO(
  "A New Hope",
  4,
  "Princess Leia is captured...",
  Series.SKYWALKER_SAGA,
  Trilogy.ORIGINAL,
  LocalDate.of(1977, 5, 25),
  "George Lucas",
  listOf("George Lucas"),
  listOf("George Lucas"),
  listOf("Gary Kurtz"),
  8.6
)

private val movieJson =
  "{\"_links\":{\"self\":{\"href\":\"http://127.0.0.1:8080/star-wars/movies/60ac1ae25a74bf51382c469e\"}}," +
    "\"id\":\"60ac1ae25a74bf51382c469e\"," +
    "\"title\":\"A New Hope\"," +
    "\"episode_id\":4," +
    "\"storyline\":\"Princess Leia is captured...\"," +
    "\"series\":\"SKYWALKER_SAGA\"," +
    "\"trilogy\":\"ORIGINAL\"," +
    "\"release_date\":\"1977-05-25\"," +
    "\"director\":\"George Lucas\"," +
    "\"screenwriters\":[\"George Lucas\"]," +
    "\"storyBy\":[\"George Lucas\"]," +
    "\"producers\":[\"Gary Kurtz\"]," +
    "\"imdb_score\":8.6," +
    "\"created\":[2021,6,2,6,30,40,50000]," +
    "\"edited\":[2021,6,2,6,30,40,50000]}"
