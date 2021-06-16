package com.github.vitormbgoncalves.starwarsmovies.infrastructure.health

import cn.zenliu.ktor.redis.RedisFactory
import io.lettuce.core.codec.StringCodec
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.component.KoinComponent
import org.slf4j.LoggerFactory

/**
 * Redis health check
 *
 * @author Vitor Goncalves
 * @since 10.06.2021, qui, 19:05
 */

object RedisHealthCheck : KoinComponent {

  private val client = RedisFactory.newReactiveClient(StringCodec.UTF8)

  private val logger = LoggerFactory.getLogger(RedisHealthCheck::class.java)

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
