package it.ipzs.androidpidprovider.network.datasource

import it.ipzs.androidpidprovider.network.response.CredentialResponse
import it.ipzs.androidpidprovider.network.response.ParResponse
import it.ipzs.androidpidprovider.network.response.TokenResponse
import retrofit2.http.*

internal interface PidProviderService {

    @FormUrlEncoded
    @Headers("Accept: application/json",
        "Content-Type: application/x-www-form-urlencoded")
    @POST("as/par")
    suspend fun requestPar(
        @Field("responseType") responseType: String,
        @Field("clientId") clientId: String,
        @Field("codeChallenge") codeChallenge: String,
        @Field("codeChallengeMethod") codeChallengeMethod: String,
        @Field("clientAssertionType") clientAssertionType: String,
        @Field("clientAssertion") clientAssertion: String,
        @Field("request") request: String
    ): ParResponse

    @FormUrlEncoded
    @POST("token")
    suspend fun requestToken(
        @Header("DPoP") dPop: String,
        @Field("grantType") grantType: String,
        @Field("clientId") clientId: String,
        @Field("code") code: String,
        @Field("codeVerifier") codeVerifier: String,
        @Field("clientAssertionType") clientAssertionType: String,
        @Field("clientAssertion") clientAssertion: String,
        @Field("redirectUri") redirectUri: String
    ): TokenResponse

    @FormUrlEncoded
    @POST("credential")
    suspend fun requestCredential(
        @Header("DPoP") dPop: String,
        @Header("Authorization") authorization: String,
        @Field("credentialDefinition") credentialDefinition: String,
        @Field("format") format: String,
        @Field("proof") proof: String?
    ): CredentialResponse

}