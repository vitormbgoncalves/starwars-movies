package com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas

import com.auth0.jwk.JwkProviderBuilder
import com.papsign.ktor.openapigen.model.Described
import com.papsign.ktor.openapigen.model.security.HttpSecurityScheme
import com.papsign.ktor.openapigen.model.security.SecuritySchemeModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeType
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.typesafe.config.ConfigFactory
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.util.pipeline.PipelineContext
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.reflect.typeOf

/**
 * OpenAPI Generator JWT authentication
 *
 * @author Vitor Goncalves
 * @since 12.06.2021, sáb, 14:49
 */

@OptIn(ExperimentalStdlibApi::class)
fun Application.installAuth() {

  val jwkIssuer = environment.config.property("jwt.jwkIssuer").getString()
  val jwksUrl = URL(environment.config.property("jwt.jwksURL").getString())
  val jwkRealm = environment.config.property("jwt.jwkRealm").getString()
  val audience = environment.config.property("jwt.audience").getString()
  val jwkProvider = JwkProviderBuilder(jwksUrl)
    .cached(10, 24, TimeUnit.HOURS)
    .rateLimited(10, 1, TimeUnit.MINUTES)
    .build()

  fun installJwt(provider: Authentication.Configuration) {
    provider.apply {
      jwt("jwt") {
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

object JwtProvider : AuthProvider<JWTPrincipal> {
  override val security: Iterable<Iterable<AuthProvider.Security<*>>> =
    listOf(
      listOf(
        AuthProvider.Security(
          SecuritySchemeModel(
            SecuritySchemeType.http,
            scheme = HttpSecurityScheme.bearer,
            bearerFormat = "JWT",
            name = "JWT"
          ),
          emptyList<JwtScopes>()
        )
      )
    )

  override suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): JWTPrincipal {
    return pipeline.context.authentication.principal() ?: throw RuntimeException("No JWTPrincipal")
  }

  @ExperimentalStdlibApi
  override fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<JWTPrincipal> {
    val authenticatedKtorRoute = route.ktorRoute.authenticate("jwt") { }
    return OpenAPIAuthenticatedRoute(
      authenticatedKtorRoute,
      route.provider.child().also { it.registerModule(this, typeOf<AuthProvider<*>>()) },
      this
    )
  }
}

enum class JwtScopes(override val description: String) : Described {
  Dummy("dummy jwt scope")
}

@ExperimentalStdlibApi
inline fun NormalOpenAPIRoute.jwtAuth(crossinline route: OpenAPIAuthenticatedRoute<JWTPrincipal>.() -> Unit = {}): OpenAPIAuthenticatedRoute<JWTPrincipal> {
  return JwtProvider.apply(this).apply {
    route()
  }
}
