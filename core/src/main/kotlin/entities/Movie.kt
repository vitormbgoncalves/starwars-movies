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
  @Contextual val registrationDate: LocalDateTime = LocalDateTime.now(),
  val title: String,
  val description: String,
  val series: Series,
  val trilogy: Trilogy?,
  @Contextual val usReleaseDate: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val imdbScore: Double
)

enum class Trilogy {
  ORIGINAL, PREQUEL, SEQUEL
}

enum class Series {
  SKYWALKER_SAGA, ANIMATED, ANTHOLOGY
}
