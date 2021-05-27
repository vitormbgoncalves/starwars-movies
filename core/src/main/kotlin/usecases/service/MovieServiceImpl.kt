package com.github.vitormbgoncalves.starwarsmovies.core.usecases.service

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository.IMovieRepository

/**
 * Movie service implementation
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 17:14
 */

class MovieServiceImpl(
  private val movieRepository: IMovieRepository
) :
  IMovieService {
  override suspend fun findAll(page: Int, size: Int): List<Movie> {
    return movieRepository.findAll(page, size)
  }

  override suspend fun findById(id: String): Movie? {
    return movieRepository.findById(id)
  }

  override suspend fun create(movie: Movie): Movie {
    return movieRepository.create(movie)
  }

  override suspend fun update(id: String, movie: Movie): Movie? {
    return movieRepository.update(id, movie)
  }

  override suspend fun delete(id: String) {
    return movieRepository.delete(id)
  }

  override suspend fun totalMovies(): Long {
    return movieRepository.totalMovies()
  }
}
