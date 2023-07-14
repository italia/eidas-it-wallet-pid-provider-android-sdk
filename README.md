
EUDI-IT-Wallet-Pid-Provider-Android-Sdk

EUDI-IT-Wallet-Pid-Provider-Android-Sdk is an sdk developed in Kotlin for Android OS that include the functionality to obtain the Italian PID (Person Identification Data) credential in according to [Italian EUDI Wallet Technical Specifications](https://italia.github.io/eudi-wallet-it-docs/en/pid-data-model.html)

# Technical requirements:

EUDI-IT-Wallet-Pid-Provider-android-sdk is compatible with android min sdk 23 and above. Is mandatory to have internet connection and NFC technology.

# Integration requirements:

//TODO

# How to use it:

In the sdk is present a demo application called AndroidPidProviderDemo that show how to integrate it easily.

# Configuration :

This method is used for initialize the EUDI-IT-Wallet-Pid-Provider-Android-Sdk :


    initialize(context: Context, pidProviderConfig: PidProviderConfig? = null)

	
	class PidProviderConfig internal constructor(
	    var logEnabled: Boolean? = false,
	    private var baseUrl:String? = null,
	    private var walletInstanceAttestation: String? = null,
	    private var walletUri: String? = null
	) : Serializable  
	

	
	Initialize method is used to configure sdk with following parameters : 

	- log enabled : if true enable sdk logs
	- baseUrl : Url for the PID provider api needed by the sdk
 	- walletInstanceAttestation : As defined in the techical specification
	- walletUri : Domain of the wallet application

After initialize the sdk methods are to be called in the following order:

- initJwtForPar(context:Context):String?
  This method is used for create Jwt for par request described in PKCE flow. Returns an unsigned JWT that the calling application will have to sign it and pass it into the next API startAuthFlow.

- fun startAuthFlow(activity: AppCompatActivity, signedJwtForPar: String, jwkForDPoP: String, pidSdkCallback: IPidSdkCallback<Boolean>){

  interface IPidSdkCallback<T> {

  fun onComplete(result: T?)

  fun onError(throwable: Throwable)

  }

This method is used for start the process to obtain the PID. It requires the signed jwt of the previous api and a jwk for the DPoP described in the PKCE Flow. Returns true when the process is finished with success result.

DPoP jwk non-normative example is present in technical specifications.

- fun getUnsignedJwtForProof(context: Context): String
  This method is used to recover the unsigned jwt. The calling application will have to sign it and pass it in the api completeAuthFlow to retrieve the credentials.

- fun completeAuthFlow(activity: AppCompatActivity, signedJwtForProof: String, pidSdkCallback: IPidSdkCallback<PidCredential>)
  This method complete the PKCE flow and returns the italian PID Credential.

Pid Credential Result : 

data class PidCredential(
   var format: String?,
   var credential: String?,
   var nonce: String?,
   var nonceExpires: Long?
) : Serializable


# License: Apache License Version 2.0





 

 


