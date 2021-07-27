@file:Suppress("LongParameterList", "PrintStackTrace", "MaxLineLength")
package com.github.vitormbgoncalves.restapi

/**
 * Generate, decoder and verify JWT token
 *
 * @author Vitor Goncalves
 * @since 03.05.2021, seg, 09:21
 */

import com.google.gson.GsonBuilder
import org.apache.commons.codec.binary.Base64
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import kotlin.text.Charsets.UTF_8

object JWT {

  private const val verifyAlgorithm = "SHA256withRSA"
  private const val tokenSignatureAlgorithm = "SHA256withRSA"
  private const val keyAlgorithm = "RSA"
  private const val tokenDelimiter = '.'

  fun <H : JWTAuthHeader, P : JWTAuthPayload> token(
    header: H,
    payload: P,
    secret: String,
    jsonEncoder: JsonEncoder<H, P>,
    encoder: Base64Encoder,
    decoder: Base64Decoder,
    charset: Charset = UTF_8
  ): String {

    val headerString = jsonEncoder.toJson(header)
    val payloadString = jsonEncoder.toJson(payload)

    val base64Header = encoder.encodeURLSafe(headerString.toByteArray(charset))
    val base64Payload = encoder.encodeURLSafe(payloadString.toByteArray(charset))

    val value = "$base64Header$tokenDelimiter$base64Payload"

    return value + tokenDelimiter + rs256(secret, value, encoder, decoder, charset)
  }

  fun <H : JWTAuthHeader, P : JWTAuthPayload> decode(
    jwtTokenString: String,
    jsonDecoder: JsonDecoder<H, P>,
    decoder: Base64Decoder,
    charset: Charset = UTF_8
  ): JWTToken<H, P>? {
    val parts = jwtTokenString.split(tokenDelimiter)
    return if (parts.size >= 2) {

      val headerJson = decoder.decode(parts[0].toByteArray(charset)).toString(charset)
      val payloadJson = decoder.decode(parts[1].toByteArray(charset)).toString(charset)

      val header: H = jsonDecoder.headerFrom(headerJson)
      val payload: P = jsonDecoder.payloadFrom(payloadJson)

      if (parts.size == 3) {
        val signature = decoder.decode(parts[2].toByteArray(charset))
        JWTToken(header, payload, signature)
      } else {
        JWTToken(header, payload)
      }
    } else {
      null
    }
  }

  fun verify(jwt: String, jwk: JWKObject, decoder: Base64Decoder, charset: Charset = UTF_8): Boolean {

    val rsa = jwk.toRSA(decoder)

    return if (rsa == null) {
      false
    } else {
      val parts = jwt.split(tokenDelimiter)

      if (parts.size == 3) {
        val header = parts[0].toByteArray(charset)
        val payload = parts[1].toByteArray(charset)
        val tokenSignature = decoder.decode(parts[2])

        val rsaSignature = Signature.getInstance(verifyAlgorithm)
        rsaSignature.initVerify(rsa)
        rsaSignature.update(header)
        rsaSignature.update(tokenDelimiter.toByte())
        rsaSignature.update(payload)
        rsaSignature.verify(tokenSignature)
      } else {
        false
      }
    }
  }

  private fun rs256(
    secret: String,
    data: String,
    encoder: Base64Encoder,
    decoder: Base64Decoder,
    charset: Charset
  ): String {

    val factory = KeyFactory.getInstance(keyAlgorithm)
    val keySpec = PKCS8EncodedKeySpec(decoder.decode(secret.toByteArray(charset)))
    val key = factory.generatePrivate(keySpec)

    val algECDSAsha256 = Signature.getInstance(tokenSignatureAlgorithm)
    algECDSAsha256.initSign(key)
    algECDSAsha256.update(data.toByteArray(charset))

    return encoder.encodeURLSafe(algECDSAsha256.sign())
  }
}

/**
 * Mapper to transform auth header and payload to a json String.
 */
interface JsonEncoder<H : JWTAuthHeader, P : JWTAuthPayload> {
  /**
   * Transforms the provided header to a json String.
   * @param header The header to transform.
   * @return A json String representing the header.
   */
  fun toJson(header: H): String

  /**
   * Transforms the provided payload to a json String.
   * @param payload The header to transform.
   * @return A json String representing the payload.
   */
  fun toJson(payload: P): String
}

interface JsonDecoder<H : JWTAuthHeader, P : JWTAuthPayload> {
  /**
   * Transforms the provided header json String into a header object.
   * @return A header object representing the json String.
   */
  fun headerFrom(json: String): H

  /**
   * Transforms the provided payload json String into a payload object.
   * @return A payload object representing the json String.
   */
  fun payloadFrom(json: String): P
}

interface Base64Encoder {
  /**
   * Base64 encodes the provided bytes in an URL safe way.
   * @param bytes The ByteArray to be encoded.
   * @return The encoded String.
   */
  fun encodeURLSafe(bytes: ByteArray): String

  /**
   * Base64 encodes the provided bytes.
   * @param bytes The ByteArray to be encoded.
   * @return The encoded String.
   */
  fun encode(bytes: ByteArray): String
}

interface Base64Decoder {
  /**
   * Base64 encodes the provided bytes.
   * @param bytes The ByteArray to be decoded.
   * @return The decoded bytes.
   */
  fun decode(bytes: ByteArray): ByteArray

  /**
   * Base64 encodes the provided String.
   * @param string The String to be decoded.
   * @return The decoded String as a ByteArray.
   */
  fun decode(string: String): ByteArray
}

/**
 * JWTToken representation with header and payload. Used for String token decoding.
 */
open class JWTToken<H : JWTAuthHeader, P : JWTAuthPayload>(
  val header: H,
  val payload: P,
  val signature: ByteArray? = null
)

/**
 * An object representing a Json Web Key (JWK).
 */
open class JWKObject(
  val kty: String,
  val kid: String,
  val use: String,
  val alg: String,
  val n: String,
  val e: String
) {
  /**
   * Turns the JWK into an RSA public key.
   * @return A valid RSA public key.
   */
  open fun toRSA(decoder: Base64Decoder): PublicKey? {

    return try {
      val kf = KeyFactory.getInstance("RSA")

      val modulus = BigInteger(1, decoder.decode(n))
      val exponent = BigInteger(1, decoder.decode(e))
      return kf.generatePublic(RSAPublicKeySpec(modulus, exponent))
    } catch (e: InvalidKeySpecException) {
      e.printStackTrace()
      null
    } catch (e: NoSuchAlgorithmException) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Turns the JWK into an RSA public key in String format.
   * @return A valid RSA public key String.
   */
  @Suppress("ReturnCount")
  open fun toRSAString(encoder: Base64Encoder, decoder: Base64Decoder): String? {

    return try {
      val rsa = toRSA(decoder) ?: return null

      val kf = KeyFactory.getInstance("RSA")
      val spec: X509EncodedKeySpec = kf.getKeySpec(rsa, X509EncodedKeySpec::class.java)
      return encoder.encode(spec.encoded)
    } catch (e: InvalidKeySpecException) {
      e.printStackTrace()
      null
    } catch (e: NoSuchAlgorithmException) {
      e.printStackTrace()
      null
    }
  }
}

/**
 * JWT Authentication token header.
 */
open class JWTAuthHeader(
  val alg: String,
  val typ: String,
  val kid: String,
)

/**
 * JWT authentication token payload.
 */
open class JWTAuthPayload(
  val exp: Long,
  val iat: Long,
  val iss: String,
  val aud: String
)

/**
 * JWT token creating implementation
 */
object jwtAuth {

  private val gson = GsonBuilder().create()

  // generic JSON encoder
  private val jsonEncoder = object : JsonEncoder<JWTAuthHeader, JWTAuthPayload> {
    override fun toJson(header: JWTAuthHeader): String {
      return gson.toJson(header, JWTAuthHeader::class.java)
    }

    override fun toJson(payload: JWTAuthPayload): String {
      return gson.toJson(payload, JWTAuthPayload::class.java)
    }
  }

  // Base64 encoder using apache commons
  private val encoder = object : Base64Encoder {
    override fun encodeURLSafe(bytes: ByteArray): String {
      return Base64.encodeBase64URLSafeString(bytes)
    }

    override fun encode(bytes: ByteArray): String {
      return Base64.encodeBase64String(bytes)
    }
  }

  // Base64 decoder using apache commons
  private val decoder = object : Base64Decoder {
    override fun decode(bytes: ByteArray): ByteArray {
      return Base64.decodeBase64(bytes)
    }

    override fun decode(string: String): ByteArray {
      return Base64.decodeBase64(string)
    }
  }

  fun createToken(secret: String, header: JWTAuthHeader, payload: JWTAuthPayload): String {
    return JWT.token(header, payload, secret, jsonEncoder, encoder, decoder)
  }
}
