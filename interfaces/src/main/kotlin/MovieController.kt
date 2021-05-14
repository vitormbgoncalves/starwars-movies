package com.github.vitormbgoncalves.starwarsmovies.interfaces

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.service.MovieService
import java.time.LocalDate
import java.time.LocalDateTime

class MovieController(val movieService: MovieService) {
  suspend fun getAllMovies(): List<MovieDTO> {
    return movieService.findAll().map { it.toMovieDTO() }
  }

  suspend fun getMovie(name: String): MovieDTO {
    return movieService.findByName(name).toMovieDTO()
  }

  suspend fun createMovie(movieDTO: MovieDTO): Boolean {
    return movieService.create(movieDTO.toMovie())
  }

  suspend fun updateMovie(id: Int, movieDTO: MovieDTO): Boolean {
    return movieService.update(id, movieDTO.toMovie())
  }

  suspend fun deleteMovie(movieDTO: MovieDTO): Boolean {
    return movieService.delete(movieDTO.toMovie())
  }
}

data class MovieDTO(
  val title: String,
  val description: String,
  val series: Series,
  val trilogy: Trilogy?,
  val usReleaseDate: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdbScore: Long
)

private fun Movie.toMovieDTO() =
  MovieDTO(title, description, series, trilogy, usReleaseDate, director, screenwriters, storyBy, producers, imdbScore)

private fun MovieDTO.toMovie() = Movie(
  registrationDate = LocalDateTime.now(),
  title = title,
  description = description,
  series = series,
  trilogy = trilogy,
  usReleaseDate = usReleaseDate,
  director = director,
  screenwriters = screenwriters,
  storyBy = storyBy,
  producers = producers,
  imdbScore = imdbScore
)
