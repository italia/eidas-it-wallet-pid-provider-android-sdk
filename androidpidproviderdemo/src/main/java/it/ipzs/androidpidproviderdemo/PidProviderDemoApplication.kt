@file:Suppress("PrivatePropertyName")

package it.ipzs.androidpidproviderdemo

import android.app.Application
import android.security.keystore.KeyProperties
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.KeyUse
import it.ipzs.androidpidprovider.external.PidProviderConfig
import it.ipzs.androidpidprovider.external.PidProviderSdk
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.*

class PidProviderDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeSDK()
    }

    private fun initializeSDK() {
        val walletInstance = "{\n" +
                "  \"iss\": \"https://wallet-provider.example.org\",\n" +
                "  \"sub\": \"thumprint-of-the-jwk-in-the-cnf-identifiying-the-wallet\",\n" +
                "  \"iat\": 1665137911,\n" +
                "  \"exp\": 1665138911,\n" +
                "  \"type\": \"WalletInstanceAttestation\",\n" +
                "  \"supported_LoA\": \"high\",\n" +
                "  \"policy_uri\": \"https://wallet-provider.example.com/privacy_policy\",\n" +
                "  \"tos_uri\": \"https://wallet-provider.example.com/info_policy\",\n" +
                "  \"logo_uri\": \"https://wallet-provider.example.com/sgd-cmyk-150dpi-90mm.svg\",\n" +
                "  \"cnf\": {\n" +
                "    \"jwk\": {\n" +
                "        \"kty\": \"EC\",\n" +
                "        \"kid\": \"wallet-pub-jwk-kid\",\n" +
                "        \"crv\": \"P-256\",\n" +
                "        \"x\": \"a1MdTboSUbq4SOx4LmhOI2AewVkZWDQD0gP9nOiSnHU\",\n" +
                "        \"y\": \"f8n1IgpfYOBFZM0KxkTd0N5Y2P-INNmU_6S-gDro_FE\"\n" +
                "   }\n" +
                "  }\n" +
                "}\n"

        val pidProviderConfig = PidProviderConfig
            .Builder()
            .baseUrl("https://api.wakala.it/it-pid-provider/")
            .walletInstance(walletInstance)
            .walletUri("https://www.google.com")
            .logEnabled(true)
            .build()
        PidProviderSdk.initialize(
            applicationContext,
            pidProviderConfig = pidProviderConfig
        )
    }

}