package it.ipzs.androidpidprovider.external

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import it.ipzs.androidpidprovider.exception.PIDProviderException
import it.ipzs.androidpidprovider.utils.PidProviderSDKUtils
import it.ipzs.androidpidprovider.utils.PidSdkCallbackManager
import it.ipzs.androidpidprovider.view.PidProviderActivity

object PidProviderSdk {

    fun initialize(context: Context, pidProviderConfig: PidProviderConfig? = null) {
        pidProviderConfig?.let {
            PidProviderSDKUtils.configure(context,pidProviderConfig)
        }?: kotlin.run {
            throw PIDProviderException("pid provider config can't be null")
        }
    }

    fun startAuthFlow(activity: AppCompatActivity, pidSdkCallback: IPidSdkCallback){
        PidProviderActivity.start(activity)
        PidSdkCallbackManager.setSdkCallback(pidSdkCallback)
    }

}