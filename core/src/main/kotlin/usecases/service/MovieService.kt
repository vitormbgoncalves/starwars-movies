package com.github.vitormbgoncalves.starwarsmovies.core.usecases.service

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie

interface MovieService {
  suspend fun findAll(): List<Movie>
  suspend fun findByName(name: String): Movie
  suspend fun create(movie: Movie): Boolean
  suspend fun update(id: Int, movie: Movie): Boolean
  suspend fun delete(movie: Movie): Boolean
}
