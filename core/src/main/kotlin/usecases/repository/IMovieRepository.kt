package com.github.vitormbgoncalves.starwarsmovies.core.usecases.repository

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie

interface IMovieRepository {
  suspend fun findAll(): List<Movie>
  suspend fun findByName(name: String): Movie
  suspend fun create(movie: Movie): Boolean
  suspend fun update(id: Int, movie: Movie): Boolean
  suspend fun delete(movie: Movie): Boolean
}
