package it.ipzs.androidpidprovider.utils

import it.ipzs.androidpidprovider.external.IPidSdkCallback
import it.ipzs.androidpidprovider.external.PidCredential


internal object PidSdkCallbackManager {

    private var sdkCallback: IPidSdkCallback? = null

    fun setSdkCallback(sdkCallback: IPidSdkCallback){
        PidSdkCallbackManager.sdkCallback = sdkCallback
    }

    fun invokeOnComplete(pidCredential: PidCredential) {
        sdkCallback?.onComplete(pidCredential)
    }

    fun invokeOnError(throwable: Throwable){
        sdkCallback?.onError(throwable)
    }

}