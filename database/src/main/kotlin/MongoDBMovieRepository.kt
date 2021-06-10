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
 * MongoDB repository implementation with KMongo
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 18:12
 */

class MongoDBMovieRepository(client: CoroutineClient) : IMovieRepository {

  var moviesCollection: CoroutineCollection<Movie>

  init {
    val database = client.getDatabase("movies")
    moviesCollection = database.getCollection("movies")
  }

  override suspend fun findAll(page: Int, size: Int): List<Movie> {
    val skips = page * size
    return moviesCollection.find().skip(skips).limit(size).toList().asIterable().map { it }
  }

  override suspend fun findById(id: String): Movie {
    return moviesCollection.findOneById(ObjectId(id))
      ?: throw IllegalArgumentException("movie with the given id not found!")
  }

  override suspend fun create(movie: Movie): Movie {
    try {
      moviesCollection.insertOne(movie)
      return movie
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }

  override suspend fun update(id: String, movie: Movie): Movie {
    try {
      moviesCollection.updateOne(
        Movie::id eq ObjectId(id).toId(),
        set(
          Movie::title setTo movie.title,
          Movie::episodeId setTo movie.episodeId,
          Movie::storyline setTo movie.storyline,
          Movie::series setTo movie.series,
          Movie::trilogy setTo movie.trilogy,
          Movie::releaseDate setTo movie.releaseDate,
          Movie::director setTo movie.director,
          Movie::screenwriters setTo movie.screenwriters,
          Movie::storyBy setTo movie.storyBy,
          Movie::producers setTo movie.producers,
          Movie::imdbScore setTo movie.imdbScore,
          Movie::edited setTo LocalDateTime.now()
        )
      )
      return moviesCollection.findOneById(ObjectId(id))
        ?: throw IllegalArgumentException("movie with the given id not found!")
    } catch (e: Exception) {
      throw IllegalArgumentException(e.localizedMessage)
    }
  }

  override suspend fun delete(id: String) {
    when (moviesCollection.deleteOneById(ObjectId(id)).deletedCount) {
      0L -> throw IllegalArgumentException("movie with the given id not found!")
    }
  }

  override suspend fun totalMovies(): Long {
    return moviesCollection.estimatedDocumentCount()
  }
}
