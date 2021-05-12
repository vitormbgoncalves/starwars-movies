package com.github.vitormbgoncalves.core.entities

import java.time.LocalDate

data class Movie(
  val id: String,
  val registrationDate: LocalDate,
  val title: String,
  val series: Series,
  val trilogy: Trilogy?,
  val US_releaseDate: LocalDate,
  val director: String,
  val screenwriters: List<String>,
  val storyBy: List<String>,
  val producers: List<String>,
  val IMDB_score: Long
)

enum class Trilogy {
  ORIGINAL, PREQUEL, SEQUEL
}

enum class Series {
  SKYWALKER_SAGA, ANIMATED, ANTHOLOGY
}
