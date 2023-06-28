@file:Suppress("PrivatePropertyName", "SpellCheckingInspection")

package it.ipzs.androidpidprovider.keystore

import android.content.Context
import android.util.Log
import it.ipzs.androidpidprovider.utils.LogHelper
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

class KeystoreHelper {

    private val EC_PRIVATE_KEY_ALIAS = "EC_PRIVATE_KEY_ALIAS"
    private val EC_PUBLIC_KEY_ALIAS = "EC_PRIVATE_KEY_ALIAS"

    private var encryptor: KeystoreEncryptor? = null
    private var decryptor: KeystoreDecryptor? = null

    fun init() {
        encryptor = KeystoreEncryptor()
        decryptor = KeystoreDecryptor()
        decryptor?.init()
    }

    fun saveEcPrivateKey(ecPrivateKey: ECPrivateKey?) {
        try {
            encryptor?.encryptText(EC_PRIVATE_KEY_ALIAS, ecPrivateKey.toString())
        } catch (e: Exception) {
            Log.d("KeystoreHelper", e.toString())
        }
    }

    @Suppress("unused")
    fun getEcPrivateKey(context: Context): String? {
        return try {
            decryptor?.decryptData(
                EC_PRIVATE_KEY_ALIAS,
                encryptor?.getEncryption(),
                encryptor?.getIv()
            )
        } catch (e: Exception) {
            LogHelper.d(context, "KeystoreHelper", e.toString())
            null
        }
    }

    fun saveEcPublicKey(context: Context, ecPublicKey: ECPublicKey?) {
        try {
            encryptor?.encryptText(EC_PUBLIC_KEY_ALIAS, ecPublicKey.toString())
        } catch (e: Exception) {
            LogHelper.d(context, "KeystoreHelper", e.toString())
        }
    }

    @Suppress("unused")
    fun getEcPublicKey(context: Context): String? {
        return try {
            decryptor?.decryptData(
                EC_PUBLIC_KEY_ALIAS,
                encryptor?.getEncryption(),
                encryptor?.getIv()
            )
        } catch (e: Exception) {
            LogHelper.d(context, "KeystoreHelper", e.toString())
            null
        }
    }
}