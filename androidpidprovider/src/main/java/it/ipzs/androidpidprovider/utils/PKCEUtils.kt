package it.ipzs.androidpidprovider.utils

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.util.Base64URL
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.ipzs.androidpidprovider.constant.AlgorithmConstant
import it.ipzs.androidpidprovider.entity.WalletInstanceEntity
import it.ipzs.androidpidprovider.storage.PidProviderSDKShared
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.interfaces.ECPrivateKey
import java.util.*
import kotlin.collections.HashMap

internal object PKCEUtils {

    fun createCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val code = ByteArray(32)
        secureRandom.nextBytes(code)
        return Base64.encodeToString(
            code,
            Base64.NO_PADDING or
                    Base64.DEFAULT or
                    Base64.URL_SAFE
        )
    }

    fun createCodeChallenge(codeVerifier: String): String {
        val bytesCodeVerifier = codeVerifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance(AlgorithmConstant.ALGORITHM_SHA_256)
        messageDigest.update(bytesCodeVerifier, 0, bytesCodeVerifier.size)
        val digest: ByteArray = messageDigest.digest()
        return Base64.encodeToString(
            digest,
            Base64.NO_PADDING or
                    Base64.DEFAULT or
                    Base64.URL_SAFE or
                    Base64.NO_WRAP
        )
    }

    fun computeThumbprint(walletInstanceJsonString: String): String {
        val gson = Gson()
        val walletInstanceEntity =
            gson.fromJson(walletInstanceJsonString, WalletInstanceEntity::class.java)
        val jwk = walletInstanceEntity.cnf?.jwk
        val jwkJsonString = gson.toJson(jwk)

        val ecKey: ECKey = ECKey.parse(jwkJsonString)
        val thumbprint: Base64URL = ecKey.computeThumbprint()
        return thumbprint.decodeToString()
    }

    fun generateJWTForPar(
        jwkThumbprint: String,
        codeChallenge: String,
        redirectUri: String,
        walletInstanceJsonString: String,
        privateKey: ECPrivateKey,
    ): String {
        val mapHeaderClaims = HashMap<String, Any>()
        mapHeaderClaims[PKCEConstant.HEADER_JWT_ALG_KEY] = PKCEConstant.HEADER_JWT_ALG_VALUE
        mapHeaderClaims[PKCEConstant.HEADER_JWT_KID_KEY] = Base64.encodeToString(
            Base64URL.encode(jwkThumbprint).decode(),
            Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP
        )
        val jsonObjectCredentialDefinition = JsonObject().apply {
            addProperty(PKCEConstant.JWT_KEY_CLAIM, PKCEConstant.JWT_CLAIM_VALUE)
        }
        val jsonObjectAuthDetails = JsonObject().apply {
            addProperty(PKCEConstant.JWT_KEY_TYPE, PKCEConstant.JWT_TYPE_VALUE)
            addProperty(PKCEConstant.JWT_KEY_FORMAT, PKCEConstant.JWT_FORMAT_VALUE)
            add(PKCEConstant.JWT_CREDENTIAL_DEFINITION_KEY, jsonObjectCredentialDefinition)
        }
        val mapClaimsJwt = getMapClaimsForPar(
            jwkThumbprint,
            codeChallenge,
            redirectUri,
            walletInstanceJsonString,
            jsonObjectAuthDetails
        )
        val jwtBuilder = Jwts.builder()
        jwtBuilder.setHeader(mapHeaderClaims)
        for ((key, value) in mapClaimsJwt) {
            jwtBuilder.claim(key, value)
        }

        return jwtBuilder
            .signWith(SignatureAlgorithm.ES256, privateKey)
            .compact()
    }

    fun generateJWTForProof(
        context: Context,
        jwkThumbprint: String,
        nonce: String,
        privateKey: ECPrivateKey,
    ): String {
        val pidProviderUrl = PidProviderSDKShared.getInstance(context).getBaseURL()
        val mapHeaderClaims = HashMap<String, Any>()
        mapHeaderClaims[PKCEConstant.HEADER_JWT_ALG_KEY] = PKCEConstant.HEADER_JWT_ALG_VALUE
        mapHeaderClaims[PKCEConstant.HEADER_JWT_KID_KEY] = Base64.encodeToString(
            privateKey.encoded,
            Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP
        )
        mapHeaderClaims[PKCEConstant.HEADER_JWT_TYP_KEY] = PKCEConstant.HEADER_JWT_TYP_VALUE
        val jwtBuilder = Jwts.builder()
        jwtBuilder.setHeader(mapHeaderClaims)
        val mapClaimProof = getMapClaimsForProof(jwkThumbprint, pidProviderUrl, nonce)
        for ((key, value) in mapClaimProof) {
            jwtBuilder.claim(key, value)
        }
        return jwtBuilder
            .signWith(SignatureAlgorithm.ES256, privateKey)
            .compact()
    }

    private fun getMapClaimsForPar(
        jwkThumbprint: String,
        codeChallenge: String,
        redirectUri: String,
        walletInstanceAttestation: String,
        jsonObjectAuthDetails: JsonObject
    ): HashMap<String, Any> {
        val mapClaims = HashMap<String, Any>()
        mapClaims[PKCEConstant.JWT_RESPONSE_TYPE_KEY] = PKCEConstant.JWT_RESPONSE_TYPE_VALUE
        mapClaims[PKCEConstant.JWT_RESPONSE_TYPE_KEY] = PKCEConstant.JWT_RESPONSE_TYPE_VALUE
        mapClaims[PKCEConstant.JWT_CLIENT_ID_KEY] = jwkThumbprint
        mapClaims[PKCEConstant.JWT_CODE_CHALLENGE_KEY] = codeChallenge
        mapClaims[PKCEConstant.JWT_CODE_CHALLENGE_METHOD_KEY] =
            PKCEConstant.JWT_CODE_CHALLENGE_METHOD_VALUE
        mapClaims[PKCEConstant.JWT_CODE_CHALLENGE_METHOD_KEY] =
            PKCEConstant.JWT_CODE_CHALLENGE_METHOD_VALUE
        mapClaims[PKCEConstant.JWT_REDIRECT_URI_KEY] = redirectUri

        mapClaims[PKCEConstant.JWT_CLIENT_ASSERTION_TYPE_KEY] =
            PKCEConstant.JWT_CLIENT_ASSERTION_TYPE_VALUE
        mapClaims[PKCEConstant.JWT_CLIENT_ASSERTION_KEY] = walletInstanceAttestation
        mapClaims[PKCEConstant.JWT_AUTHORIZATION_DETAILS_KEY] = Gson().toJson(jsonObjectAuthDetails)
        mapClaims[PKCEConstant.JWT_STATE_KEY] = UUID.randomUUID().toString()
        return mapClaims
    }

    private fun getMapClaimsForProof(
        jwkThumbprint: String,
        pidProviderUrl: String,
        nonce: String
    ): HashMap<String, Any> {
        val mapClaims = HashMap<String, Any>()
        mapClaims[PKCEConstant.JWT_ISS_KEY] = jwkThumbprint
        mapClaims[PKCEConstant.JWT_AUD_KEY] = pidProviderUrl
        mapClaims[PKCEConstant.JWT_IAT_KEY] = Date()
        mapClaims[PKCEConstant.JWT_NONCE_KEY] = nonce
        return mapClaims
    }
}