package com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas

import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam

/**
 * OpenAPI Generator parameters
 *
 * @author Vitor Goncalves
 * @since 26.05.2021, qua, 21:17
 */

@Path("{id}")
data class StringParam(@PathParam("Movie id param") val id: String)

data class PageQuery(
  @QueryParam("Page number") val page: Int,
  @QueryParam("Page size") val size: Int
)

@Suppress("EnumNaming")
enum class Tag(override val description: String) : APITag {
  `Star Wars films`("V.1 API.")
}
