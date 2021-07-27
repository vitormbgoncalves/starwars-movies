@file:Suppress("MagicNumber", "MaxLineLength")
package com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas

import com.github.vitormbgoncalves.starwarsmovies.core.entities.Series
import com.github.vitormbgoncalves.starwarsmovies.core.entities.Trilogy
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.Curries
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.HalLink
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.PagingInfo
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.RequestMovieDTO
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseAllMovies
import com.github.vitormbgoncalves.starwarsmovies.interfaces.dto.ResponseMovieDTO
import java.time.LocalDate

/**
 * OpenAPI doc examples
 *
 * @author Vitor Goncalves
 * @since 25.05.2021, ter, 11:50
 */

val responseMovie: ResponseMovieDTO = ResponseMovieDTO(
  links = HalLink(mapOf("href" to "http://")),
  id = "60ac1ae25a74bf51382c469e",
  title = "A New Hope",
  episodeId = 4,
  storyline = "Princess Leia is captured and held hostage by the evil Imperial forces in their effort to take over the galactic Empire...",
  series = Series.SKYWALKER_SAGA,
  trilogy = Trilogy.ORIGINAL,
  releaseDate = LocalDate.of(1977, 5, 25),
  director = "George Lucas",
  screenwriters = listOf("George Lucas"),
  storyBy = listOf("George Lucas"),
  producers = listOf("Gary Kurtz"),
  imdbScore = 8.6
)

val requestMovie: RequestMovieDTO = RequestMovieDTO(
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

val responseAllMovies: ResponseAllMovies = ResponseAllMovies(
  HalLink(
    mapOf("href" to "http://"),
    mapOf("href" to "http://"),
    mapOf("href" to "http://"),
    mapOf("href" to "http://"),
    mapOf("href" to "http://"),
    Curries("ns", "http://")
  ),
  mapOf("ns:movies" to listOf(responseMovie)),
  PagingInfo(1, 1)
)
