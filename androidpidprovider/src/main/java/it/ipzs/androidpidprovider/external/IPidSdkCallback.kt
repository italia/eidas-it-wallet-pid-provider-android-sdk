package it.ipzs.androidpidprovider.external

interface IPidSdkCallback {

    fun onComplete(pidCredential: PidCredential?)

    fun onError(throwable: Throwable)

}