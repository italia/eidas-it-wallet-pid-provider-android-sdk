package it.ipzs.androidpidprovider.utils

import android.content.Context
import it.ipzs.androidpidprovider.exception.PIDProviderException
import it.ipzs.androidpidprovider.extension.isValidUrl
import it.ipzs.androidpidprovider.external.PidProviderConfig
import it.ipzs.androidpidprovider.keystore.KeystoreHelper
import it.ipzs.androidpidprovider.storage.PidProviderSDKShared
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

internal object PidProviderSDKUtils {

    private val keystoreHelper by lazy {  KeystoreHelper()}

    fun configure(context:Context,pidProviderConfig: PidProviderConfig){
        saveBaseUrl(context, pidProviderConfig.getBaseURL())
        saveLogEnabled(context, pidProviderConfig.isLogEnabled())
        initKeyStoreHelper()
        savePrivateKey(context, pidProviderConfig.getPrivateKey())
        savePublicKey(context, pidProviderConfig.getPublicKey())
        saveWalletInstance(context, pidProviderConfig.getWalletInstanceAttestation())
        saveWalletUri(context, pidProviderConfig.getWalletUri())
    }

    private fun initKeyStoreHelper(){
        keystoreHelper.init()
    }

    private fun saveBaseUrl(context: Context, baseUrl: String) {
        if (baseUrl.isNotEmpty()) {
            if(baseUrl.isValidUrl()) {
                PidProviderSDKShared.getInstance(context).saveBaseURL(baseUrl)
            } else {
                throw PIDProviderException("base url is not valid")
            }
        } else {
            throw PIDProviderException("base url can't be empty")
        }
    }

    private fun saveLogEnabled(context: Context, isLogEnabled: Boolean) {
        PidProviderSDKShared.getInstance(context).saveLogEnabled(isLogEnabled)
    }

    private fun savePrivateKey(context: Context, ecPrivateKey: ECPrivateKey?) {
        if (ecPrivateKey != null) {
            keystoreHelper.saveEcPrivateKey(ecPrivateKey)
            PidProviderSDKShared.getInstance(context).savePrivateKey(ecPrivateKey)
        } else {
            throw PIDProviderException("private key can't be null")
        }
    }

    private fun savePublicKey(context: Context, ecPublicKey: ECPublicKey?) {
        if (ecPublicKey != null) {
            keystoreHelper.saveEcPublicKey(context,ecPublicKey)
            PidProviderSDKShared.getInstance(context).savePublicKey(ecPublicKey)
        } else {
            throw PIDProviderException("public key can't be null")
        }
    }

    private fun saveWalletInstance(context: Context, walletInstance: String?) {
        if (!walletInstance.isNullOrEmpty()) {
            PidProviderSDKShared.getInstance(context).saveWalletInstanceAttestation(walletInstance)
        } else {
            throw PIDProviderException("wallet instance attestation can't be empty")
        }
    }

    private fun saveWalletUri(context: Context, walletUri: String?) {
        if (!walletUri.isNullOrEmpty()) {
            PidProviderSDKShared.getInstance(context).saveWalletUri(walletUri)
        } else {
            throw PIDProviderException("wallet uri can't be empty")
        }
    }
}