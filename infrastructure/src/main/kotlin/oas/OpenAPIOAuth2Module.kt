@file:Suppress("LongParameterList", "FunctionNaming", "MagicNumber", "UnusedPrivateMember", "EnumNaming")
package com.github.vitormbgoncalves.starwarsmovies.infrastructure.oas

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.model.Described
import com.papsign.ktor.openapigen.model.security.FlowsModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeType
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.throws
import com.typesafe.config.ConfigFactory
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.OAuthAccessTokenResponse
import io.ktor.auth.OAuthAccessTokenResponse.OAuth2
import io.ktor.auth.OAuthServerSettings
import io.ktor.auth.OAuthServerSettings.OAuth2ServerSettings
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.origin
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.host
import io.ktor.request.path
import io.ktor.request.port
import io.ktor.util.pipeline.PipelineContext

/**
 * OpenAPI Generator OAuth2 authentication
 *
 * @author Vitor Goncalves
 * @since 12.06.2021, sáb, 14:49
 */

class OAuth2Handler<A, T>(
  val settings: OAuth2ServerSettings,
  private val implicitScopes: List<T>? = null,
  private val passwordScopes: List<T>? = null,
  private val clientCredentialsScopes: List<T>? = null,
  private val authorizationCodeScopes: List<T>? = null,
  val httpClient: HttpClient = HttpClient(Apache),
  val urlProvider: ApplicationCall.(OAuthServerSettings) -> String = { defaultURLProvider(it) },
  private val auth: suspend (principal: OAuth2) -> A?
) where T : Enum<T>, T : Described {
  val authName = settings.name

  private class BadPrincipalException : Exception("Could not get principal from token")

  suspend fun getOAuth(principal: OAuth2): A =
    auth(principal) ?: throw BadPrincipalException()

  private val flows = FlowsModel<T>().apply {
    if (implicitScopes != null) implicit(
      implicitScopes, settings.authorizeUrl,
      settings.accessTokenUrl
    )
    if (passwordScopes != null) password(
      passwordScopes, settings.accessTokenUrl,
      settings.accessTokenUrl
    )
    if (clientCredentialsScopes != null) clientCredentials(
      clientCredentialsScopes, settings.accessTokenUrl,
      settings.accessTokenUrl
    )
    if (authorizationCodeScopes != null) authorizationCode(
      authorizationCodeScopes,
      settings.authorizeUrl,
      settings.accessTokenUrl,
      settings.accessTokenUrl
    )
  }

  val scheme = SecuritySchemeModel(SecuritySchemeType.oauth2, settings.name, flows = flows)

  private inner class OAuth2Provider(scopes: List<T>) : AuthProvider<A> {
    override suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): A =
      getOAuth(pipeline.call.principal() ?: throw IllegalArgumentException("No JWTPrincipal"))

    override fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<A> =
      OpenAPIAuthenticatedRoute(route.ktorRoute.authenticate(authName) {}, route.provider.child(), this).throws(
        APIException.apiException<BadPrincipalException>(HttpStatusCode.Unauthorized)
      )

    override val security: Iterable<Iterable<AuthProvider.Security<*>>> =
      listOf(listOf(AuthProvider.Security(scheme, scopes)))
  }

  fun auth(apiRoute: NormalOpenAPIRoute, scopes: List<T>): OpenAPIAuthenticatedRoute<A> {
    val authProvider = OAuth2Provider(scopes)
    return authProvider.apply(apiRoute)
  }

  companion object {
    fun ApplicationCall.defaultURLProvider(settings: OAuthServerSettings): String {
      val defaultPort = if (request.origin.scheme == "http") 80 else 443
      val hostPort = request.host() + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
      val protocol = request.origin.scheme
      val uri = request.path()
      return "$protocol://$hostPort$uri"
    }
  }
}

enum class Scopes(override val description: String) : Described {
  openid("Openid scope"),
  profile("Basic Profile scope"),
  address("Adress scope"),
  phone("Phone scope"),
}

private val providerUrl = ConfigFactory.load("application.conf").getString("OAuth2.providerUrl")
private val clientId = ConfigFactory.load("application.conf").getString("OAuth2.clientId")
private val clientSecret = ConfigFactory.load("application.conf").getString("OAuth2.clientSecret")

private val oauthProvider = OAuth2ServerSettings(
  name = "Ktor-OAuth2",
  authorizeUrl = providerUrl + "authorize",
  accessTokenUrl = providerUrl + "oauth/token",
  requestMethod = HttpMethod.Post,
  clientId = clientId,
  clientSecret = clientSecret,
  defaultScopes = listOf(Scopes.openid.toString())
)

var oauth: OAuth2Handler<OAuthAccessTokenResponse, Scopes> =
  OAuth2Handler(
    oauthProvider,
    passwordScopes = listOf()
  ) { (accessToken, tokenType, expiresIn, refreshToken, extraParameters) ->
    OAuth2(
      accessToken, tokenType, expiresIn, refreshToken, extraParameters
    )
  }

@ExperimentalStdlibApi
inline fun NormalOpenAPIRoute.OAuth(
  crossinline route: OpenAPIAuthenticatedRoute<OAuthAccessTokenResponse>.() ->
  Unit = {}
): OpenAPIAuthenticatedRoute<OAuthAccessTokenResponse> {
  return oauth.auth(this, listOf()).apply {
    route()
  }
}
