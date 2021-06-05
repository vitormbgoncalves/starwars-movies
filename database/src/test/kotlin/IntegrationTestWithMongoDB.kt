package com.github.vitormbgoncalves.starwarsmovies.database.test

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository.IMovieRepository
import com.github.vitormbgoncalves.starwarsmovies.database.MongoDBMovieRepository
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.coInvoking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.id.toId
import org.litote.kmongo.reactivestreams.KMongo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

/**
 * MongoDB integration test
 *
 * @author Vitor Goncalves
 * @since 04.06.2021, sex, 16:51
 */

object IntegrationTestWithMongoDB : Spek({

  lateinit var mongodExe: MongodExecutable
  lateinit var mongod: MongodProcess
  lateinit var movieRepository: IMovieRepository
  val client = KMongo.createClient("mongodb://localhost:28017").coroutine

  beforeGroup {
    mongodExe = MongodStarter.getDefaultInstance().prepare(
      MongodConfig.builder()
        .version(Version.Main.PRODUCTION)
        .net(Net("localhost", 28017, Network.localhostIsIPv6()))
        .build()
    )
    mongod = mongodExe.start()
    movieRepository = MongoDBMovieRepository(client)
  }

  afterGroup {
    mongod.stop()
    mongodExe.stop()
  }

  describe("MongoDB integration test") {

    it("creat movie") {
      runBlocking {
        movieRepository.create(movie1) shouldBeEqualTo movie1
      }
    }

    it("do not create movie with repeated id") {
      runBlocking {
        coInvoking { movieRepository.create(movie2) } shouldThrow IllegalArgumentException::class withMessage
          "E11000 duplicate key error collection: movies.movies index: _id_ dup key: " +
          "{ : ObjectId('60bba776d0686920739c3cf5') }"
      }
    }

    it("find movie by id") {
      runBlocking {
        movieRepository.findById("60bba776d0686920739c3cf5") shouldBeEqualTo movie1
      }
    }

    it("do not find movie with incorrect id") {
      runBlocking {
        movieRepository.findById("60bba776d0686920739c3cf9").shouldBeNull()
        coInvoking {
          movieRepository.findById("60bba776d0686920739c3cf")
        } shouldThrow IllegalArgumentException::class withMessage
          "invalid hexadecimal representation of an ObjectId: [60bba776d0686920739c3cf]"
      }
    }

    it("find all movies") {
      runBlocking {
        movieRepository.findAll(0, 2) shouldContain movie1
      }
    }

    it("update movie") {
      runBlocking {
        movieRepository.update("60bba776d0686920739c3cf5", movie2) shouldBeInstanceOf Movie::class
      }
    }

    it("do not update movie with incorrect id") {
      runBlocking {
        movieRepository.update("60bba776d0686920739c3cf9", movie2).shouldBeNull()
        coInvoking {
          movieRepository.update(
            "60bba776d0686920739c3cf",
            movie2
          )
        } shouldThrow IllegalArgumentException::class withMessage
          "invalid hexadecimal representation of an ObjectId: [60bba776d0686920739c3cf]"
      }
    }

    it("count movies collection") {
      runBlocking {
        movieRepository.totalMovies() shouldBeEqualTo 1
      }
    }

    it("delete movie") {
      runBlocking {
        movieRepository.delete("60bba776d0686920739c3cf5")
        movieRepository.findById("60bba776d0686920739c3cf5").shouldBeNull()
      }
    }

    it("do not delete movie with incorrect id") {
      runBlocking {
        coInvoking {
          movieRepository.delete("60bba776d0686920739c3cf")
        } shouldThrow IllegalArgumentException::class withMessage
          "invalid hexadecimal representation of an ObjectId: [60bba776d0686920739c3cf]"
      }
    }
  }
})

private val movie1 = Movie(
  ObjectId("60bba776d0686920739c3cf5").toId(),
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
  LocalDateTime.of(2021, Month.JUNE, 2, 6, 30, 40),
  LocalDateTime.of(2021, Month.JUNE, 2, 6, 30, 40)
)

private val movie2 = Movie(
  ObjectId("60bba776d0686920739c3cf5").toId(),
  "A New Hope????",
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
  LocalDateTime.of(2021, Month.JUNE, 2, 6, 30, 40),
  LocalDateTime.of(2021, Month.JUNE, 2, 6, 30, 40)
)
