package com.github.vitormbgoncalves.starwarsmovies.interfaces.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.papsign.ktor.openapigen.annotations.Response
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data transfer object for movies response
 *
 * @author Vitor Goncalves
 * @since 24.05.2021, seg, 16:00
 */

@Response("Movie Reponse")
data class ResponseMovieDTO(
  val id: String,
  val title: String,
  val episode_id: Long? = null,
  val storyline: String,
  val series: Series,
  val trilogy: Trilogy? = null,
  @JsonSerialize(using = ToStringSerializer::class)
  @JsonDeserialize(using = LocalDateDeserializer::class)
  val release_date: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdb_score: Double,
  val created: LocalDateTime = LocalDateTime.now(),
  val edited: LocalDateTime = LocalDateTime.now(),
  val url: String = "http://0.0.0.0:8080/star-wars/movies/$id"
)

fun Movie.toResponseMovieDTO() = ResponseMovieDTO(
  id = id.toString(),
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
  imdb_score = imdb_score,
  created = created,
  edited = edited
)
