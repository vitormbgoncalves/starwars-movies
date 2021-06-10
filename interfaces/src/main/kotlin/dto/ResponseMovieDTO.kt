package com.github.vitormbgoncalves.starwarsmovies.interfaces.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Movie
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.papsign.ktor.openapigen.annotations.Response
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data transfer object for movies response
 *
 * @author Vitor Goncalves
 * @since 24.05.2021, seg, 16:00
 */

@Serializable
@Response("Movie Reponse")
data class ResponseMovieDTO(
  @Contextual
  @SerialName("_links")
  @JsonProperty("_links")
  val links: HalLink,
  val id: String,
  val title: String,
  val episodeId: Long? = null,
  val storyline: String,
  val series: Series,
  val trilogy: Trilogy? = null,
  @Contextual
  @JsonSerialize(using = ToStringSerializer::class)
  @JsonDeserialize(using = LocalDateDeserializer::class)
  val releaseDate: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdbScore: Double,
  @Contextual
  val created: LocalDateTime = LocalDateTime.now(),
  @Contextual
  val edited: LocalDateTime = LocalDateTime.now()
)

/*
* Data transfer object mapper
*/

private val uri = ConfigFactory.load("application.conf").getString("hypermedia.uri")

fun Movie.toResponseMovieDTO() = ResponseMovieDTO(
  links = HalLink(mapOf("href" to "$uri/movies/$id")),
  id = id.toString(),
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
  imdbScore = imdbScore,
  created = created,
  edited = edited
)

fun Movie.toResponseAllMovies() = ResponseMovieDTO(
  links = HalLink(mapOf("href" to "/movies/$id")),
  id = id.toString(),
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
  imdbScore = imdbScore,
  created = created,
  edited = edited
)
