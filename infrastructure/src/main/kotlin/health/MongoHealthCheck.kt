package com.github.vitormbgoncalves.starwarsmovies.infrastructure.health

import com.mongodb.BasicDBObject
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineClient
import org.slf4j.LoggerFactory

/**
 * MongoDB health check
 *
 * @author Vitor Goncalves
 * @since 10.06.2021, qui, 19:05
 */

object MongoHealthCheck : KoinComponent {

  private val client: CoroutineClient by inject()

  private val logger = LoggerFactory.getLogger(MongoHealthCheck::class.java)

  suspend fun check(): Boolean {
    return try {
      val db = withTimeoutOrNull(5000L) {
        client.getDatabase("movies").runCommand<BasicDBObject>(BasicDBObject("ping", "1"))
      }
      db?.let {
        return true
      } ?: run {
        throw IllegalArgumentException("Database connection was broken!")
      }
    } catch (e: Exception) {
      logger.trace("MongoHealthCheck: ${e.localizedMessage}")
      false
    }
  }
}
