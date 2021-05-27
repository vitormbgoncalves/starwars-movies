package com.github.vitormbgoncalves.starwarsmovies.core.usecases.service

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie

/**
 * Movie service interface
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 17:14
 */

interface IMovieService {
  suspend fun findAll(page: Int, size: Int): List<Movie>
  suspend fun findById(id: String): Movie?
  suspend fun create(movie: Movie): Movie
  suspend fun update(id: String, movie: Movie): Movie?
  suspend fun delete(id: String)
  suspend fun totalMovies(): Long
}
