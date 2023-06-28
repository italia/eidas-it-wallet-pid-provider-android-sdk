

# Eidas-it-wallet-pid-provider-android-sdk

Eidas-it-wallet-pid-provider-android-sdk is an sdk developed in Kotlin that include the functionality to obtain the PID (Person Identification Data) credential.

# Technical requirements:

Eidas-it-wallet-pid-provider-android-sdk is compatible with android min sdk 23 and above. Is mandatory to have internet connection and NFC technology.

# Integration requirements:

//TODO

# How to use it:

In the sdk is present a demo application called AndroidPidProviderDemo that show how to integrate it easily.

# Configuration :

This method is used for intialize the Eidas-it-wallet-pid-provider-android-sdk :

	
    initialize(context: Context, pidProviderConfig: PidProviderConfig? = null)

	
	class PidProviderConfig internal constructor(
	    var logEnabled: Boolean? = false,
	    private var baseUrl:String? = null,
	    private var ecPrivateKey: ECPrivateKey,
	    private var ecPublicKey: ECPublicKey,
	    private var walletInstanceAttestation: String? = null,
	    private var walletUri: String? = null
	) : Serializable  
	

	
	Initialize method is used to configure sdk with following parameters : 

	- log enabled : if true enable sdk logs
	- baseUrl : Url for the PID provider api needed by the sdk
	- ecPrivateKey : Elliptic curve private key used for the wallet instance attestation 
	- ecPublicKey : Elliptic curve public key used for the wallet instance attestation 
 	- walletInstanceAttestation : 

		example : 

			{
			  "iss": "https://wallet-provider.example.org",
			  "sub": "$thumprint-of-the-jwk-in-the-cnf-identifiying-the-wallet",
			  "iat": 1665137911,
			  "exp": 1665138911,
			  "type": "WalletInstanceAttestation",
			  "supported_LoA": "high",
			  "policy_uri": "https://wallet-provider.example.com/privacy_policy",
			  "tos_uri": "https://wallet-provider.example.com/info_policy",
			  "logo_uri": "https://wallet-provider.example.com/sgd-cmyk-150dpi-90mm.svg",
			  "cnf": {
			    "jwk": {
		             "kty": "EC",
		             "kid": "wallet-pub-jwk-kid",
		             "crv": "P-256",
		             "x": "a1MdTboSUbq4SOx4LmhOI2AewVkZWDQD0gP9nOiSnHU",
		             "y": "f8n1IgpfYOBFZM0KxkTd0N5Y2P-INNmU_6S-gDro_FE"
			   }
			  }
			}

	- walletUri : Url where the wallet application is register by app links/deepLink 

This method is used for start the process to obtain the PID :


startAuthFlow(activity: AppCompatActivity, pidSdkCallback: IPidSdkCallback)


	
	interface IPidSdkCallback {

	    fun onComplete(pidResult:PIDResult)

	    fun onError(throwable: Throwable)

	}
	


# License: Apache License Version 2.0





 

 


