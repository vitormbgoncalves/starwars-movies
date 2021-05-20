package com.github.vitormbgoncalves.starwarsmovies.interfaces

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.core.usecases.service.IMovieService
import java.time.LocalDate

/**
 * Controller for movies
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 17:16
 */

class MovieController(private val movieService: IMovieService) {
  suspend fun getAllMovies(): List<MovieDTO> {
    return movieService.findAll().map { it.toMovieDTO() }
  }

  suspend fun getMovie(id: String): MovieDTO? {
    return movieService.findById(id)?.toMovieDTO()
  }

  suspend fun createMovie(movie: NewMovieDTO): Boolean {
    return movieService.create(movie.toMovie())
  }

  suspend fun updateMovie(id: String, movie: NewMovieDTO): Boolean {
    return movieService.update(id, movie.toMovie())
  }

  suspend fun deleteMovie(id: String): Boolean {
    return movieService.delete(id)
  }
}

/*
* Data transfer objects for Movie
*/
data class MovieDTO(
  val id: String,
  val title: String,
  val description: String,
  val series: Series,
  val trilogy: Trilogy?,
  @JsonSerialize(using = ToStringSerializer::class)
  @JsonDeserialize(using = LocalDateDeserializer::class)
  val usReleaseDate: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdbScore: Double
)

data class NewMovieDTO(
  val title: String,
  val description: String,
  val series: Series,
  val trilogy: Trilogy?,
  @JsonSerialize(using = ToStringSerializer::class)
  @JsonDeserialize(using = LocalDateDeserializer::class)
  val usReleaseDate: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdbScore: Double
)

private fun Movie.toMovieDTO() = MovieDTO(
  id = id.toString(),
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

private fun NewMovieDTO.toMovie() = Movie(
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
