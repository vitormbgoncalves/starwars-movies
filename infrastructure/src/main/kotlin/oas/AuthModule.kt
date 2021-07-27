package com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * JWT authentication configuration
 *
 * @author Vitor Goncalves
 * @since 12.06.2021, sáb, 14:49
 */

@Suppress("MagicNumber")
@OptIn(ExperimentalStdlibApi::class)
fun Application.installAuth() {

  val jwkIssuer = environment.config.property("OAuth2.jwkIssuer").getString()
  val jwksUrl = URL(environment.config.property("OAuth2.jwksURL").getString())
  val jwkRealm = environment.config.property("OAuth2.jwkRealm").getString()
  val audience = environment.config.property("OAuth2.audience").getString()
  val jwkProvider = JwkProviderBuilder(jwksUrl)
    .cached(10, 24, TimeUnit.HOURS)
    .rateLimited(10, 1, TimeUnit.MINUTES)
    .build()

  fun installJwt(provider: Authentication.Configuration) {
    provider.apply {
      jwt("Ktor-OAuth2") {
        /*skipWhen { environment.config.property("ktor.development").getString() == "true" }*/
        verifier(jwkProvider, jwkIssuer)
        realm = jwkRealm
        validate { credentials ->
          log.debug("Credentials audience: ${credentials.payload.audience}")
          log.debug("Credentials issuer: ${credentials.payload.issuer}")
          if (credentials.payload.audience.contains(audience)) {
            JWTPrincipal(credentials.payload)
          } else {
            null
          }
        }
      }
    }
  }

  install(Authentication) {
    installJwt(this)
  }
}
