package com.github.vitormbgoncalves.starwarsmovies.database

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository.IMovieRepository
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.id.toId
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import java.time.LocalDateTime

/**
 * MongoDB repository implementation with KMongo - a Kotlin toolkit for Mongo
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 18:12
 */

class MongoDBMovieRepository(client: CoroutineClient) : IMovieRepository {

  lateinit var moviesCollection: CoroutineCollection<Movie>

  init {
    val database = client.getDatabase("movies")
    moviesCollection = database.getCollection<Movie>("movies")
  }

  override suspend fun findAll(page: Int, size: Int): List<Movie> {
    return try {
      val skips = page * size
      moviesCollection.find().skip(skips).limit(size).toList().asIterable().map { it }
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }

  override suspend fun findById(id: String): Movie? {
    return try {
      moviesCollection.findOneById(ObjectId(id))
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }

  override suspend fun create(movie: Movie): Movie {
    return try {
      moviesCollection.insertOne(movie)
      movie
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }

  override suspend fun update(id: String, movie: Movie): Movie? {
    return try {
      moviesCollection.updateOne(
        Movie::id eq ObjectId(id).toId(),
        set(
          Movie::title setTo movie.title,
          Movie::episode_id setTo movie.episode_id,
          Movie::storyline setTo movie.storyline,
          Movie::series setTo movie.series,
          Movie::trilogy setTo movie.trilogy,
          Movie::release_date setTo movie.release_date,
          Movie::director setTo movie.director,
          Movie::screenwriters setTo movie.screenwriters,
          Movie::storyBy setTo movie.storyBy,
          Movie::producers setTo movie.producers,
          Movie::imdb_score setTo movie.imdb_score,
          Movie::edited setTo LocalDateTime.now()
        )
      )
      moviesCollection.findOneById(ObjectId(id))
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }

  override suspend fun delete(id: String) {
    try {
      moviesCollection.deleteOneById(ObjectId(id))
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }

  override suspend fun totalMovies(): Long {
    return moviesCollection.estimatedDocumentCount()
  }
}
