package com.menzus.jwt4s

import java.time.Clock

import com.menzus.jwt4s.internal.Algorithm.createSignature
import com.menzus.jwt4s.internal.Header.asHeaderBase64
import com.menzus.jwt4s.internal.Header.createHeader
import com.menzus.jwt4s.internal.Payload.createClaimsFor

trait Signer {
  def signTokenFor(subject: String, roles: Set[String]): Token
}

case class Token(idToken: String, expiresIn: Long)

object Signer {

  def apply(settings: SignerSettings)(implicit clock: Clock) = new Signer {

    implicit val _settings = settings

    def signTokenFor(subject: String, roles: Set[String]): Token = {
      val header          = createHeader(settings.algorithm)
      val headerBase64    = asHeaderBase64(header)
      val claimsBase64    = createClaimsFor(subject, roles)
      val signatureBase64 = createSignature(header, headerBase64, claimsBase64)

      Token(
        idToken = concat(headerBase64, claimsBase64, signatureBase64),
        expiresIn = settings.expiresInS
      )
    }


    private def concat(headerBase64: String, payloadBase64: String, signatureBase64: String) = {
      List(headerBase64, payloadBase64, signatureBase64).mkString(".")
    }
  }
}