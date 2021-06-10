package com.github.vitormbgoncalves.starwarsmovies.interfaces.test

import com.github.vitormbgoncalves.starwarsmovies.commonlib.mapper.Json
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.service.MovieServiceImpl
import com.github.vitormbgoncalves.starwarsmovies.interfaces.controller.MovieController
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.RequestMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseAllMovies
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import org.amshove.kluent.coInvoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.litote.kmongo.toId
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

/**
 * Movie controller test
 *
 * @author Vitor Goncalves
 * @since 02.06.2021, qua, 10:05
 */

object MovieControllerTest : Spek({

  describe("Request movies") {

    val mockMovieService = mockk<MovieServiceImpl>(relaxed = true)

    val movieController = MovieController(mockMovieService)

    beforeEachTest {
      clearMocks(mockMovieService)
    }

    it("get all movies") {
      runBlocking {
        coEvery { mockMovieService.findAll(any(), any()) } returns listOf(movie)
        val movies = movieController.getAllMovies(1, 1)
        Json.encodeToString(movies) shouldContain movieJson1
        coVerify { mockMovieService.findAll(1, 1) }
      }
    }

    it("get movie by id") {
      runBlocking {
        coEvery { mockMovieService.findById("60afdbe0b3c7c176f1f7988c") } returns movie
        val movie = movieController.getMovie("60afdbe0b3c7c176f1f7988c")
        Json.encodeToString(movie) shouldBeEqualTo movieJson2
        coVerify { mockMovieService.findById("60afdbe0b3c7c176f1f7988c") }
      }
    }
    it("do not get movie with incorret id") {
      runBlocking {
        coEvery { mockMovieService.findById(any()) } returns null
        movieController.getMovie("60afdbe0b3c7c176f1f7988d").shouldBeNull()
        coVerify { mockMovieService.findById("60afdbe0b3c7c176f1f7988d") }
      }
    }
    it("creat movie") {
      runBlocking {
        coEvery { mockMovieService.create(any()) } returns movie
        val movie = movieController.createMovie(requestMovie)
        Json.encodeToString(movie) shouldBeEqualTo movieJson2
        coVerify { mockMovieService.create(any()) }
      }
    }
    it("update movie") {
      runBlocking {
        coEvery { mockMovieService.update(any(), any()) } returns movie
        val movie = movieController.updateMovie("60afdbe0b3c7c176f1f7988c", requestMovie)
        Json.encodeToString(movie) shouldBeEqualTo movieJson2
        coVerify { mockMovieService.update(any(), any()) }
      }
    }
    it("do not update movie with incorret id") {
      runBlocking {
        coEvery { mockMovieService.update(any(), any()) } returns null
        movieController.updateMovie("60afdbe0b3c7c176f1f7988c", requestMovie).shouldBeNull()
        coVerify { mockMovieService.update(any(), any()) }
      }
    }
    it("delete movie") {
      runBlocking {
        coEvery { mockMovieService.delete(any()) } returns Unit
        movieController.deleteMovie("60afdbe0b3c7c176f1f7988c")
        coVerify { mockMovieService.delete("60afdbe0b3c7c176f1f7988c") }
      }
    }
    it("get movies with pagination") {
      runBlocking {
        coEvery { mockMovieService.findAll(any(), any()) } returns listOf(movie)
        coEvery { mockMovieService.totalMovies() } returns 1
        coInvoking { movieController.getMoviesPage(0, 0) } shouldThrow IllegalArgumentException::class withMessage
          "/ by zero"
        val movies = movieController.getMoviesPage(1, 1)
        movies shouldBeInstanceOf ResponseAllMovies::class
        Json.encodeToString(movies) shouldBeEqualTo allMoviesJson
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

private const val movieJson1 =
  "[{\"_links\":{" +
    "\"self\":{\"href\":\"http://127.0.0.1:8080/star-wars/movies/60ac1ae25a74bf51382c469e\"}}," +
    "\"id\":\"60ac1ae25a74bf51382c469e\"," +
    "\"title\":\"A New Hope\"," +
    "\"episodeId\":4," +
    "\"storyline\":\"Princess Leia is captured...\"," +
    "\"series\":\"SKYWALKER_SAGA\"," +
    "\"trilogy\":\"ORIGINAL\"," +
    "\"releaseDate\":\"1977-05-25\"," +
    "\"director\":\"George Lucas\"," +
    "\"screenwriters\":[\"George Lucas\"]," +
    "\"storyBy\":[\"George Lucas\"]," +
    "\"producers\":[\"Gary Kurtz\"]," +
    "\"imdbScore\":8.6," +
    "\"created\":\"2021/06/02 06:30:40\"," +
    "\"edited\":\"2021/06/02 06:30:40\"}]"

private const val movieJson2 =
  "{\"_links\":{" +
    "\"self\":{\"href\":\"http://127.0.0.1:8080/star-wars/movies/60ac1ae25a74bf51382c469e\"}}," +
    "\"id\":\"60ac1ae25a74bf51382c469e\"," +
    "\"title\":\"A New Hope\"," +
    "\"episodeId\":4," +
    "\"storyline\":\"Princess Leia is captured...\"," +
    "\"series\":\"SKYWALKER_SAGA\"," +
    "\"trilogy\":\"ORIGINAL\"," +
    "\"releaseDate\":\"1977-05-25\"," +
    "\"director\":\"George Lucas\"," +
    "\"screenwriters\":[\"George Lucas\"]," +
    "\"storyBy\":[\"George Lucas\"]," +
    "\"producers\":[\"Gary Kurtz\"]," +
    "\"imdbScore\":8.6," +
    "\"created\":\"2021/06/02 06:30:40\"," +
    "\"edited\":\"2021/06/02 06:30:40\"}"

private const val allMoviesJson =
  "{\"_links\":{" +
    "\"self\":{\"href\":\"http://127.0.0.1:8080/star-wars/movies?page=1&size=1\"}," +
    "\"first\":{\"href\":\"http://127.0.0.1:8080/star-wars/movies/?page=1&size=1\"}," +
    "\"prev\":{\"href\":null}," +
    "\"next\":{\"href\":null}," +
    "\"last\":{\"href\":\"http://127.0.0.1:8080/star-wars/movies?page=1&size=1\"}," +
    "\"curries\":{\"name\":\"ns\",\"href\":\"http://127.0.0.1:8080/star-wars\"}}," +
    "\"_embedded\":{" +
    "\"ns:movies\":[{" +
    "\"_links\":{" +
    "\"self\":{\"href\":\"/movies/60ac1ae25a74bf51382c469e\"}}," +
    "\"id\":\"60ac1ae25a74bf51382c469e\"," +
    "\"title\":\"A New Hope\"," +
    "\"episodeId\":4," +
    "\"storyline\":\"Princess Leia is captured...\"," +
    "\"series\":\"SKYWALKER_SAGA\"," +
    "\"trilogy\":\"ORIGINAL\"," +
    "\"releaseDate\":\"1977-05-25\"," +
    "\"director\":\"George Lucas\"," +
    "\"screenwriters\":[\"George Lucas\"]," +
    "\"storyBy\":[\"George Lucas\"]," +
    "\"producers\":[\"Gary Kurtz\"]," +
    "\"imdbScore\":8.6," +
    "\"created\":\"2021/06/02 06:30:40\"," +
    "\"edited\":\"2021/06/02 06:30:40\"}]}," +
    "\"info\":{\"count\":1,\"pages\":1}}"
