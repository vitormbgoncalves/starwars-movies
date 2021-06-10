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
  val episodeId: Long?,
  val storyline: String,
  val series: Series,
  val trilogy: Trilogy?,
  @JsonSerialize(using = ToStringSerializer::class)
  @JsonDeserialize(using = LocalDateDeserializer::class)
  val releaseDate: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdbScore: Double
)

fun RequestMovieDTO.toMovie() = Movie(
  title = title,
  episodeId = episodeId,
  storyline = storyline,
  series = series,
  trilogy = trilogy,
  releaseDate = releaseDate,
  director = director,
  screenwriters = screenwriters,
  storyBy = storyBy,
  producers = producers,
  imdbScore = imdbScore
)
