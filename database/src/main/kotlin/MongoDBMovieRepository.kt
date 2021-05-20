package com.github.vitormbgoncalves.starwarsmovies.database

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository.IMovieRepository
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineClient

/**
 * MongoDB repository implementation with KMongo - a Kotlin toolkit for Mongo
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 18:12
 */

class MongoDBMovieRepository(client: CoroutineClient) : IMovieRepository {

  private val moviesCollection = client.getDatabase("movies").getCollection<Movie>("movies")

  override suspend fun findAll(): List<Movie> {
    return moviesCollection.find().toList()
  }

  override suspend fun findById(id: String): Movie? {
    return moviesCollection.findOneById(ObjectId(id))
  }

  override suspend fun create(movie: Movie): Boolean {
    return moviesCollection.insertOne(movie).wasAcknowledged()
  }

  override suspend fun update(id: String, movie: Movie): Boolean {
    return moviesCollection.replaceOneById(ObjectId(id), movie).wasAcknowledged()
  }

  override suspend fun delete(id: String): Boolean {
    return moviesCollection.deleteOneById(ObjectId(id)).wasAcknowledged()
  }
}
