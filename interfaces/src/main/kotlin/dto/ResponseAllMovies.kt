package com.github.vitormbgoncalves.starwarsmovies.interfaces.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.annotations.Response

/**
 * Data transfer object for all movies response
 *
 * @author Vitor Goncalves
 * @since 24.05.2021, seg, 15:58
 */

@Response("All movies response.")
data class ResponseAllMovies(
  @JsonProperty("_links") val links: HalLink,
  @JsonProperty("_embedded")val embedded: Map<String, List<ResponseMovieDTO>>,
  val info: PagingInfo,
)

class PagingInfo(val count: Int, val pages: Int)
