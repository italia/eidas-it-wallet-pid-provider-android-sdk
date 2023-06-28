@file:Suppress("SpellCheckingInspection")

package it.ipzs.androidpidprovider.facade

import android.content.Context
import com.google.gson.Gson
import it.ipzs.androidpidprovider.entity.Proof
import it.ipzs.androidpidprovider.exception.PIDProviderException
import it.ipzs.androidpidprovider.external.PidCredential
import it.ipzs.androidpidprovider.network.datasource.PidProviderDataSource
import it.ipzs.androidpidprovider.storage.PidProviderSDKShared
import it.ipzs.androidpidprovider.utils.*
import it.ipzs.androidpidprovider.utils.DPoPUtils
import it.ipzs.androidpidprovider.utils.PKCEUtils
import it.ipzs.androidpidprovider.utils.PidProviderConfigUtils
import it.ipzs.androidpidprovider.utils.PidSdkCallbackManager
import it.ipzs.androidpidprovider.utils.UrlUtils
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

internal class PKCEFacade(
    private val context: Context,
    private val dataSource: PidProviderDataSource
) : CoroutineScope {

    suspend fun startPKCE(): String {
        val cdRequestUri = CompletableDeferred<String>()
        withContext(Dispatchers.IO) {
            try {
                val sharedPref = PidProviderSDKShared.getInstance(context)
                val codeVerifier = PKCEUtils.createCodeVerifier()
                sharedPref.saveCodeVerifier(codeVerifier)
                val codeChallenge = PKCEUtils.createCodeChallenge(codeVerifier)
                val redirectUri = sharedPref.getWalletUri()
                val walletInstanceJsonString =
                    PidProviderConfigUtils.getWalletInstanceAttestation(context)
                val jwkThumbprint = PKCEUtils.computeThumbprint(walletInstanceJsonString)
                sharedPref.saveClientId(jwkThumbprint)

                val privateKey = sharedPref.getPrivateKey()

                val parResponse = dataSource.requestPar(
                    responseType = PKCEConstant.JWT_RESPONSE_TYPE_VALUE,
                    clientId = jwkThumbprint,
                    codeChallenge = codeChallenge,
                    codeChallengeMethod = PKCEConstant.JWT_CODE_CHALLENGE_METHOD_VALUE,
                    clientAssertionType = PKCEConstant.JWT_CLIENT_ASSERTION_TYPE_VALUE,
                    clientAssertion = walletInstanceJsonString,
                    request = PKCEUtils.generateJWTForPar(
                        jwkThumbprint,
                        codeChallenge,
                        redirectUri,
                        walletInstanceJsonString,
                        privateKey
                    )
                )

                if (parResponse != null) {
                    cdRequestUri.complete(parResponse.requestUri.orEmpty())
                } else {
                    throw PIDProviderException("par response is null")
                }
            } catch (error: Throwable) {
                cdRequestUri.cancel(error.message.toString())
                PidSdkCallbackManager.invokeOnError(error)
            }
        }
        return cdRequestUri.await()
    }

    suspend fun getTokenAndCredential(code: String, redirectUri: String): PidCredential {
        val cdRequestUri = CompletableDeferred<PidCredential>()
        withContext(Dispatchers.IO) {
            try {
                val sharedPreferences = PidProviderSDKShared.getInstance(context)
                val walletInstanceJsonString =
                    PidProviderConfigUtils.getWalletInstanceAttestation(context)
                val jwkThumbprint = PKCEUtils.computeThumbprint(walletInstanceJsonString)
                val codeVerifier = sharedPreferences.getCodeVerifier()

                // Token
                val tokenUrl = UrlUtils.buildTokenUrl(context)
                val dPopToken = DPoPUtils.generateDPoP(context, "POST", tokenUrl)
                val tokenResponse = dataSource.requestToken(
                    dPop = dPopToken,
                    grantType = "authorization code",
                    clientId = jwkThumbprint,
                    code = code,
                    codeVerifier = codeVerifier,
                    clientAssertionType = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                    clientAssertion = walletInstanceJsonString,
                    redirectUri = redirectUri
                )
                if (tokenResponse != null) {
                    val accessToken = tokenResponse.accessToken.orEmpty()
                    sharedPreferences.saveToken(accessToken)
                    val privateKey = sharedPreferences.getPrivateKey()

                    // Credential
                    val credentialUrl = UrlUtils.buildCredentialUrl(context)
                    val dPopCredential = DPoPUtils.generateDPoP(context, "POST", credentialUrl)
                    val proof = Proof().apply {
                        this.proofType = PKCEConstant.PROOF_TYPE
                        this.jwt = PKCEUtils.generateJWTForProof(
                            context,
                            jwkThumbprint,
                            tokenResponse.cNonce.orEmpty(),
                            privateKey
                        )
                    }
                    val credentialResponse = dataSource.requestCredential(
                        dPop = dPopCredential,
                        authorization = accessToken,
                        credentialDefinition = PKCEConstant.CREDENTIAL_DEFINITION_VALUE,
                        format = PKCEConstant.FORMAT_CREDENTIAL_DEFINITION,
                        proof = Gson().toJson(proof)
                    )

                    if (credentialResponse != null) {
                        val pidCredential = PidCredential(
                            true,
                            credentialResponse.format,
                            credentialResponse.credential,
                            credentialResponse.cNonce,
                            credentialResponse.cNonceExpiresIn,
                        )
                        cdRequestUri.complete(pidCredential)
                    } else {
                        throw PIDProviderException("credential response is null")
                    }
                } else {
                    throw PIDProviderException("token response is null")
                }
            } catch (error: Throwable) {
                cdRequestUri.cancel(error.message.toString())
                PidSdkCallbackManager.invokeOnError(error)
            }
        }
        return cdRequestUri.await()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()
}