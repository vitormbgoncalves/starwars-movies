package com.github.vitormbgoncalves.starwarsmovies.core.usecases.service

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository.IMovieRepository

class MovieServiceImpl(
  val movieRepository: IMovieRepository
) :
  MovieService {
  override suspend fun findAll(): List<Movie> {
    return movieRepository.findAll()
  }

  override suspend fun findByName(name: String): Movie {
    return movieRepository.findByName(name)
  }

  override suspend fun create(movie: Movie): Boolean {
    return movieRepository.create(movie)
  }

  override suspend fun update(id: Int, movie: Movie): Boolean {
    return movieRepository.update(id, movie)
  }

  override suspend fun delete(movie: Movie): Boolean {
    return movieRepository.delete(movie)
  }
}
