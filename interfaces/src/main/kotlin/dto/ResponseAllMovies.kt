package com.github.vitormbgoncalves.starwarsmovies.interfaces.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.papsign.ktor.openapigen.annotations.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data transfer object for all movies response
 *
 * @author Vitor Goncalves
 * @since 24.05.2021, seg, 15:58
 */

@Serializable
@Response("All movies response.")
data class ResponseAllMovies(
  @SerialName("_links")
  @JsonProperty("_links")
  val links: HalLink,
  @SerialName("_embedded")
  @JsonProperty("_embedded")
  val embedded: Map<String, List<ResponseMovieDTO>>,
  val info: PagingInfo,
)

@Serializable
class PagingInfo(val count: Int, val pages: Int)
