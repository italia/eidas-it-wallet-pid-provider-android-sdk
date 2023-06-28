package it.ipzs.androidpidproviderdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import it.ipzs.androidpidprovider.external.IPidSdkCallback
import it.ipzs.androidpidprovider.external.PidCredential
import it.ipzs.androidpidprovider.external.PidProviderSdk

class MainDemoActivity :AppCompatActivity(), IPidSdkCallback {

    companion object {
        val TAG: String = MainDemoActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PidProviderSdk.startAuthFlow(this@MainDemoActivity,this@MainDemoActivity)
    }

    override fun onComplete(pidCredential: PidCredential?) {
        Log.d(TAG, "result: $pidCredential")
    }

    override fun onError(throwable: Throwable) {
        Log.d(TAG, "error: ${throwable.message}")
    }
}