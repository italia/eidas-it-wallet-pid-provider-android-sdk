package it.ipzs.androidpidprovider.storage

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import it.ipzs.androidpidprovider.R
import it.ipzs.androidpidprovider.utils.SingletonHolder
import okio.ByteString.Companion.decodeBase64
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.*


internal class PidProviderSDKShared private constructor(context: Context) {

    companion object :
        SingletonHolder<PidProviderSDKShared, Context>(::PidProviderSDKShared) {
        private val KEY_SECURE_KEY_SHARED_NAME = R::class.java.name.plus(".pid_provider_shared")
        private val KEY_SECURE_KEY_BASE_URL = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_base_url")
        private val KEY_SECURE_KEY_LOG_ENABLED = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_log_enabled")
        private val KEY_SECURE_KEY_WALLET_INSTANCE_ATTESTATION = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_instance_attestation")
        private val KEY_SECURE_KEY_WALLET_URI = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_wallet_uri")
        private val KEY_SECURE_KEY_CODE_VERIFIER = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_code_verifier")
        private val KEY_SECURE_KEY_CLIENT_ID = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_client_id")
        private val KEY_SECURE_KEY_TOKEN = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_token")
        private val KEY_SECURE_KEY_EC_PRIVATE = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_ec_private_key")
        private val KEY_SECURE_KEY_EC_PUBLIC = KEY_SECURE_KEY_SHARED_NAME.plus(".pid_ec_public_key")
    }

    private val sharedPreferences: SharedPreferences by lazy { getSecureSharedPreferences(context) }

    private fun getSecureSharedPreferences(context: Context): SharedPreferences {
        try {
            val spec = KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            val masterKey: MasterKey = MasterKey.Builder(context)
                .setKeyGenParameterSpec(spec)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                KEY_SECURE_KEY_SHARED_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Throwable) {
            return context.getSharedPreferences(
                KEY_SECURE_KEY_SHARED_NAME,
                Context.MODE_PRIVATE
            )
        }
    }

    // BaseUrl

    fun saveBaseURL(baseURL: String) {
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_BASE_URL, baseURL)
            apply()
        }
    }

    fun getBaseURL(): String {
        return sharedPreferences.getString(KEY_SECURE_KEY_BASE_URL, "").orEmpty()
    }

    // IsLog

    fun saveLogEnabled(logEnabled: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_SECURE_KEY_LOG_ENABLED, logEnabled)
            apply()
        }
    }

    fun isLogEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_SECURE_KEY_LOG_ENABLED, false)
    }

    // Wallet Instance Attestation

    fun saveWalletInstanceAttestation(walletInstanceAttestation: String) {
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_WALLET_INSTANCE_ATTESTATION, walletInstanceAttestation)
            apply()
        }
    }

    fun getWalletInstanceAttestation(): String {
        return sharedPreferences.getString(KEY_SECURE_KEY_WALLET_INSTANCE_ATTESTATION, "").orEmpty()
    }

    // Wallet Uri

    fun saveWalletUri(wallerUri: String) {
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_WALLET_URI, wallerUri)
            apply()
        }
    }

    fun getWalletUri(): String {
        return sharedPreferences.getString(KEY_SECURE_KEY_WALLET_URI, "").orEmpty()
    }

    // Code verifier

    fun saveCodeVerifier(codeVerifier: String) {
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_CODE_VERIFIER, codeVerifier)
            apply()
        }
    }

    fun getCodeVerifier(): String {
        return sharedPreferences.getString(KEY_SECURE_KEY_CODE_VERIFIER, "").orEmpty()
    }

    // Client Id

    fun saveClientId(clientId: String) {
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_CLIENT_ID, clientId)
            apply()
        }
    }

    fun getClientId(): String {
        return sharedPreferences.getString(KEY_SECURE_KEY_CLIENT_ID, "").orEmpty()
    }

    // Token

    fun saveToken(token: String) {
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_TOKEN, token)
            apply()
        }
    }

    fun getToken(): String {
        return sharedPreferences.getString(KEY_SECURE_KEY_TOKEN, "").orEmpty()
    }

    // Private Key

    fun savePrivateKey(privateKey: ECPrivateKey) {
        val byteArray = privateKey.encoded
        privateKey.algorithm
        val base64String = Base64.encodeToString(byteArray,
            Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP)
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_EC_PRIVATE, base64String)
            apply()
        }
    }

    fun getPrivateKey(): ECPrivateKey {
        val base64String = sharedPreferences.getString(KEY_SECURE_KEY_EC_PRIVATE, "").orEmpty()
        val byteArray = base64String.decodeBase64()?.toByteArray()
        val keyFactory = KeyFactory.getInstance("EC")
        val privateKeySpec: EncodedKeySpec = PKCS8EncodedKeySpec(byteArray)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)
        return privateKey as ECPrivateKey
    }


    // Public Key

    fun savePublicKey(publicKey: ECPublicKey) {
        val byteArray = publicKey.encoded
        val base64String = Base64.encodeToString(byteArray,
            Base64.NO_PADDING or Base64.URL_SAFE or Base64.NO_WRAP)
        sharedPreferences.edit().apply {
            putString(KEY_SECURE_KEY_EC_PUBLIC, base64String)
            apply()
        }
    }

    @Suppress("unused")
    fun getPublicKey(): ECPublicKey {
        val base64String = sharedPreferences.getString(KEY_SECURE_KEY_EC_PUBLIC, "").orEmpty()
        val byteArray = base64String.decodeBase64()?.toByteArray()
        val keyFactory = KeyFactory.getInstance("EC")
        val publicKeySpec = X509EncodedKeySpec(byteArray)
        val publicKey = keyFactory.generatePublic(publicKeySpec)
        return publicKey as ECPublicKey
    }
}