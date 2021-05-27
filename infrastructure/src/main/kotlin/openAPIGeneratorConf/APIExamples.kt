package com.github.vitormbgoncalves.starwarsmovies.infrastructure.routes

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.RequestMovieDTO
import java.time.LocalDate

/**
 * OpenAPI doc examples
 *
 * @author Vitor Goncalves
 * @since 25.05.2021, ter, 11:50
 */

val ResponseMovie: ResponseMovieDTO = ResponseMovieDTO(
  "60ac1ae25a74bf51382c469e",
  "A New Hope",
  4,
  "Princess Leia is captured and held hostage by the evil Imperial forces in their effort to take over the galactic Empire...",
  Series.SKYWALKER_SAGA,
  Trilogy.ORIGINAL,
  LocalDate.of(1977, 5, 25),
  "George Lucas",
  listOf("George Lucas"),
  listOf("George Lucas"),
  listOf("Gary Kurtz"),
  8.6
)

val RequestMovie: RequestMovieDTO = RequestMovieDTO(
  "A New Hope",
  4,
  "Princess Leia is captured and held hostage by the evil Imperial forces in their effort to take over the galactic Empire...",
  Series.SKYWALKER_SAGA,
  Trilogy.ORIGINAL,
  LocalDate.of(1977, 5, 25),
  "George Lucas",
  listOf("George Lucas"),
  listOf("George Lucas"),
  listOf("Gary Kurtz"),
  8.6
)
