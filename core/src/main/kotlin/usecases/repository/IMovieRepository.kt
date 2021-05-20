package com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie

/**
 * Repository interface
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 17:13
 */

interface IMovieRepository {
  suspend fun findAll(): List<Movie>
  suspend fun findById(id: String): Movie?
  suspend fun create(movie: Movie): Boolean
  suspend fun update(id: String, movie: Movie): Boolean
  suspend fun delete(id: String): Boolean
}
