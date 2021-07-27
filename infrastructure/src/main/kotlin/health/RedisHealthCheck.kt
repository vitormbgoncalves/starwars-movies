package com.github.vitormbgoncalves.starwarsmovies.infrastructure.health

import cn.zenliu.ktor.redis.RedisFactory
import io.lettuce.core.codec.StringCodec
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withTimeoutOrNull
import mu.KotlinLogging
import org.koin.core.component.KoinComponent

/**
 * Redis health check
 *
 * @author Vitor Goncalves
 * @since 10.06.2021, qui, 19:05
 */

@Suppress("TooGenericExceptionCaught", "MagicNumber")
object RedisHealthCheck : KoinComponent {

  private val client = RedisFactory.newReactiveClient(StringCodec.UTF8)

  private val logger = KotlinLogging.logger {}

  suspend fun check(): Boolean {
    return try {
      val redis = withTimeoutOrNull(5000L) {
        client.ping().awaitSingleOrNull()
      }
      redis?.let {
        return true
      } ?: run {
        throw IllegalArgumentException("Redis connection was broken!")
      }
    } catch (e: Exception) {
      logger.trace("RedisHealthCheck: ${e.localizedMessage}")
      false
    }
  }
}
