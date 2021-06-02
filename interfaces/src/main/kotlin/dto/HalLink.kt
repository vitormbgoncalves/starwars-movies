package com.github.vitormbgoncalves.starwarsmovies.interfaces.dto

/**
 *
 * Hal links configuration
 * @author Vitor Goncalves
 * @since 01.06.2021, ter, 15:09
 */

class HalLink(
  val self: Map<String, String>,
  val first: Map<String, String>? = null,
  val prev: Map<String, String?>? = null,
  val next: Map<String, String?>? = null,
  val last: Map<String, String>? = null,
  val curries: Curries? = null
)

class Curries(val name: String, val href: String, val templated: String = "true")
