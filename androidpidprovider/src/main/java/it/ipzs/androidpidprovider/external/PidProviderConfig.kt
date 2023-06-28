package it.ipzs.androidpidprovider.external

import java.io.Serializable
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

class PidProviderConfig internal constructor(
    var logEnabled: Boolean? = false,
    private var baseUrl: String? = null,
    private var ecPrivateKey: ECPrivateKey? = null,
    private var ecPublicKey: ECPublicKey? = null,
    private var walletInstanceAttestation: String? = null,
    private var walletUri: String? = null
) : Serializable {

    class Builder : Serializable {

        private var logEnabled: Boolean? = null
        private var baseUrl: String? = null
        private var publicKey: ECPublicKey? = null
        private var privateKey: ECPrivateKey? = null
        private var walletInstanceAttestation: String? = null
        private var walletUri: String? = null

        fun logEnabled(logEnabled: Boolean?) = apply { this.logEnabled = logEnabled }

        fun baseUrl(baseUrl: String?) = apply { this.baseUrl = baseUrl }

        fun publicKey(ecPublicKey: ECPublicKey) = apply { this.publicKey = ecPublicKey }

        fun privateKey(ecPrivateKey: ECPrivateKey) = apply { this.privateKey = ecPrivateKey }

        fun walletInstance(walletInstanceAttestation: String) =
            apply { this.walletInstanceAttestation = walletInstanceAttestation }

        fun walletUri(walletUri: String?) = apply { this.walletUri = walletUri }

        fun build(): PidProviderConfig {
            return PidProviderConfig(
                logEnabled = logEnabled,
                baseUrl = baseUrl,
                ecPrivateKey = privateKey,
                ecPublicKey = publicKey,
                walletInstanceAttestation = walletInstanceAttestation,
                walletUri = walletUri
            )
        }

    }

    fun getBaseURL(): String {
        return baseUrl.orEmpty()
    }

    fun isLogEnabled(): Boolean {
        return logEnabled ?: false
    }

    fun getPrivateKey(): ECPrivateKey? {
        return ecPrivateKey
    }

    fun getPublicKey(): ECPublicKey? {
        return ecPublicKey
    }

    fun getWalletInstanceAttestation(): String? {
        return walletInstanceAttestation
    }

    fun getWalletUri(): String? {
        return walletUri
    }

}