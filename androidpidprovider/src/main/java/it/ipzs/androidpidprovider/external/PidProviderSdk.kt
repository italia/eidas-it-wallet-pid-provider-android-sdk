package it.ipzs.androidpidprovider.external

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import it.ipzs.androidpidprovider.exception.PIDProviderException
import it.ipzs.androidpidprovider.facade.PidProviderFacade
import it.ipzs.androidpidprovider.storage.PidProviderSDKShared
import it.ipzs.androidpidprovider.utils.PidProviderSDKUtils
import it.ipzs.androidpidprovider.utils.PidSdkStartCallbackManager
import it.ipzs.androidpidprovider.utils.PidSdkCompleteCallbackManager
import it.ipzs.androidpidprovider.view.PidProviderActivity

object PidProviderSdk {

    fun initialize(context: Context, pidProviderConfig: PidProviderConfig? = null) {
        pidProviderConfig?.let {
            PidProviderSDKUtils.configure(context,pidProviderConfig)
        }?: kotlin.run {
            throw PIDProviderException("pid provider config can't be null")
        }
    }

    fun initJwtForPar(context: Context): String? {
        return PidProviderFacade(context).generateJwtForPar()
    }

    fun startAuthFlow(activity: AppCompatActivity, signedJwtForPar: String, jwkForDPoP: String, pidSdkCallback: IPidSdkCallback<Boolean>){
        PidProviderFacade(activity).startAuthFlow(activity, signedJwtForPar, jwkForDPoP)
        PidSdkStartCallbackManager.setSDKCallback(pidSdkCallback)
    }

    fun getUnsignedJwtForProof(context: Context): String {
        return PidProviderSDKShared.getInstance(context).getUnsignedJWTProof()
    }

    fun completeAuthFlow(activity: AppCompatActivity, signedJwtForProof: String, pidSdkCallback: IPidSdkCallback<PidCredential>){
        PidProviderSDKShared.getInstance(activity).saveSignedJWTProof(signedJwtForProof)
        PidProviderActivity.start(activity)
        PidSdkCompleteCallbackManager.setSDKCallback(pidSdkCallback)
    }

}