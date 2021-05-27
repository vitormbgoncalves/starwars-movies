package com.github.vitormbgoncalves.starwarsmovies.core.entities

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data class Movie
 *
 * @author Vitor Goncalves
 * @since 14.05.2021, sex, 17:12
 */

@Serializable
data class Movie(
  @Contextual @SerialName("_id") val id: Id<Movie> = newId(),
  val title: String,
  val episode_id: Long? = null,
  val storyline: String,
  val series: Series,
  val trilogy: Trilogy? = null,
  @Contextual val release_date: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdb_score: Double,
  @Contextual val created: LocalDateTime = LocalDateTime.now(),
  @Contextual val edited: LocalDateTime = LocalDateTime.now(),
)

enum class Trilogy {
  ORIGINAL, PREQUEL, SEQUEL
}

enum class Series {
  SKYWALKER_SAGA, ANIMATED, ANTHOLOGY
}
