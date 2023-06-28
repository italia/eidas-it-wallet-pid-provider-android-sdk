package it.ipzs.androidpidprovider.utils

import android.content.Context
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.oauth2.sdk.dpop.DPoPProofFactory
import com.nimbusds.oauth2.sdk.dpop.DefaultDPoPProofFactory
import it.ipzs.androidpidprovider.storage.PidProviderSDKShared
import java.net.URI
import java.security.KeyPair
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.*

internal object DPoPUtils {

    private fun getKeyPair(context: Context): KeyPair {
        val sharedPref = PidProviderSDKShared.getInstance(context)
        val privateKey = sharedPref.getPrivateKey()
        val publicKey = sharedPref.getPublicKey()
        return KeyPair(publicKey, privateKey)
    }

    private fun generateJWK(keyPair: KeyPair): ECKey? {
        return try {
            ECKey.Builder(Curve.P_256, keyPair.public as ECPublicKey)
                .privateKey(keyPair.private as ECPrivateKey)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString())
                .build()
        } catch (e:Throwable){
            null
        }
    }

    fun generateDPoP(context: Context, httpMethod: String, httpUrl: String): String {
        val keyPair = getKeyPair(context)
        val jwk = generateJWK(keyPair)
        val proofFactory: DPoPProofFactory = DefaultDPoPProofFactory(
            jwk,
            JWSAlgorithm.ES256
        )
        val proof = proofFactory.createDPoPJWT(
            httpMethod,
            URI(httpUrl)
        )
        return proof.serialize()
    }



}