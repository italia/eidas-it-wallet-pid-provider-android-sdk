package it.ipzs.androidpidproviderdemo

import android.os.Bundle
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.KeyUse
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.ipzs.androidpidprovider.external.IPidSdkCallback
import it.ipzs.androidpidprovider.external.PidCredential
import it.ipzs.androidpidprovider.external.PidProviderSdk
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.*

class MainDemoActivity :AppCompatActivity() {

    companion object {
        val TAG: String = MainDemoActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_demo)
        startSDKFlow()
    }

    private fun startSDKFlow() {
        val progress = findViewById<ProgressBar>(R.id.progress)
        val unsignedJwtForPar = PidProviderSdk.initJwtForPar(this)
        val keyPair = generateKeyPair()
        val signedJwtForPar = signJwt(keyPair, unsignedJwtForPar)
        val jwkForDPoP = generateJWK(keyPair) ?: ""
        PidProviderSdk.startAuthFlow(
            this,
            signedJwtForPar,
            jwkForDPoP,
            object : IPidSdkCallback<Boolean> {
                override fun onComplete(result: Boolean?) {
                    runOnUiThread {
                        progress.isVisible = false
                    }
                    completeAuthFlow()
                }

                override fun onError(throwable: Throwable) {
                    runOnUiThread {
                        progress.isVisible = false
                    }
                    Log.e(TAG, throwable.message.toString())
                }
            })
    }

    private fun signJwt(keyPair: KeyPair, jwtJsonString: String?): String {
        Jwts.parser().parse(jwtJsonString)
        val unsignedJwt = Jwts.parser().parse(jwtJsonString)
        return Jwts.builder()
            .setHeader(unsignedJwt.header)
            .setClaims(unsignedJwt.body as Claims)
            .signWith(SignatureAlgorithm.ES256, keyPair.private)
            .compact()
    }

    private fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC)
        keyPairGenerator.initialize(Curve.P_256.toECParameterSpec())
        return keyPairGenerator.generateKeyPair()
    }

    private fun generateJWK(keyPair: KeyPair): String? {
        return try {
            val jwk = ECKey.Builder(Curve.P_256, keyPair.public as ECPublicKey)
                .privateKey(keyPair.private as ECPrivateKey)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString())
                .build()
            jwk.toJSONString()
        } catch (e:Throwable){
            null
        }
    }

    private fun completeAuthFlow() {
        val unsignedJwtForProof = PidProviderSdk.getUnsignedJwtForProof(this)
        val keyPair = generateKeyPair()
        val signedJwtForProof = signJwt(keyPair, unsignedJwtForProof)
        PidProviderSdk.completeAuthFlow(this, signedJwtForProof, object : IPidSdkCallback<PidCredential> {
            override fun onComplete(result: PidCredential?) {
                Log.d(TAG, result.toString())
            }

            override fun onError(throwable: Throwable) {
                Log.e(TAG, throwable.message.toString())
                Toast.makeText(this@MainDemoActivity, throwable.message, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}