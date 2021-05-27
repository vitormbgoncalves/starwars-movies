package com.github.vitormbgoncalves.starwarsmovies.interfaces.dto

import com.papsign.ktor.openapigen.annotations.Response

/**
 * Data transfer object for all movies response
 *
 * @author Vitor Goncalves
 * @since 24.05.2021, seg, 15:58
 */

@Response("All movies response.")
data class ReponseAllMovies(
  val info: PagingInfo,
  val results: List<ResponseMovieDTO>
)

data class PagingInfo(var count: Int, var pages: Int, var next: Int?, var prev: Int?)
