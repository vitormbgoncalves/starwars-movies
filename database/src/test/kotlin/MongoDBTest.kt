package com.github.vitormbgoncalves.starwarsmovies.database.test

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.database.MongoDBMovieRepository
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be`
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * MongoDB testing
 *
 * @author Vitor Goncalves
 * @since 17.05.2021, seg, 16:41
 */

object MongoDBTest : Spek({

  describe("MongoDB integration testing") {

    val client =
      KMongo.createClient(
        "mongodb+srv://user:minhasenhasecreta@starwars-movies.woimo.mongodb.net/" +
          "movies?retryWrites=true&w=majority"
      ).coroutine

    val mongoRepository = MongoDBMovieRepository(client)

    it("should be OK to save Movie in database") {
      runBlocking {
        mongoRepository.create(movie) `should be` (true)
      }
    }
  }
})

private val movie = Movie(
  registrationDate = LocalDateTime.now(),
  title = "Episode IV – A New Hope",
  description = "Princess Leia is captured and held hostage by the evil Imperial " +
    "forces in their effort to take over the galactic Empire. Venturesome Luke Skywalker " +
    "and dashing captain Han Solo team together with the loveable robot duo R2-D2 and C-3PO " +
    "to rescue the beautiful princess and restore peace and justice in the Empire.",
  series = Series.SKYWALKER_SAGA,
  trilogy = Trilogy.ORIGINAL,
  usReleaseDate = LocalDate.of(1977, 5, 25),
  director = "George Lucas",
  screenwriters = listOf("George Lucas"),
  storyBy = listOf("George Lucas"),
  producers = listOf("Gary Kurtz"),
  imdbScore = 8.6
)
