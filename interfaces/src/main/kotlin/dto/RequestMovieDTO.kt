package com.github.vitormbgoncalves.starwarsmovies.interfaces.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.papsign.ktor.openapigen.annotations.Request
import java.time.LocalDate

/**
 * Data transfer object for movies request
 *
 * @author Vitor Goncalves
 * @since 24.05.2021, seg, 16:00
 */

@Request("Movie Request")
data class RequestMovieDTO(
  val title: String,
  val episode_id: Long?,
  val storyline: String,
  val series: Series,
  val trilogy: Trilogy?,
  @JsonSerialize(using = ToStringSerializer::class)
  @JsonDeserialize(using = LocalDateDeserializer::class)
  val release_date: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdb_score: Double
)

fun RequestMovieDTO.toMovie() = Movie(
  title = title,
  episode_id = episode_id,
  storyline = storyline,
  series = series,
  trilogy = trilogy,
  release_date = release_date,
  director = director,
  screenwriters = screenwriters,
  storyBy = storyBy,
  producers = producers,
  imdb_score = imdb_score
)
