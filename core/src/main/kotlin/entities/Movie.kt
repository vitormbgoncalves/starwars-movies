package com.github.vitormbgoncalves.starwarsmovies.core.entities

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Movie(
  val id: UUID = UUID.randomUUID(),
  val registrationDate: LocalDateTime,
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

enum class Trilogy {
  ORIGINAL, PREQUEL, SEQUEL
}

enum class Series {
  SKYWALKER_SAGA, ANIMATED, ANTHOLOGY
}
