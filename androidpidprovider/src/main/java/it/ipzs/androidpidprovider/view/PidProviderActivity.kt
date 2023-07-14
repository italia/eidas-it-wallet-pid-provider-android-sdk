@file:Suppress("UNUSED_VARIABLE")

package it.ipzs.androidpidprovider.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import it.ipzs.androidpidprovider.databinding.ActivityPidProviderBinding
import it.ipzs.androidpidprovider.exception.PIDProviderException
import it.ipzs.androidpidprovider.utils.PidSdkCompleteCallbackManager
import it.ipzs.androidpidprovider.view.base.ABaseActivity

internal class PidProviderActivity : ABaseActivity<ActivityPidProviderBinding>() {

    private lateinit var pidProviderViewModel: PIDProviderViewModel


    override fun setBinding(): ActivityPidProviderBinding =
        ActivityPidProviderBinding.inflate(layoutInflater)

    companion object {

        fun start(context: Context){
            val intent = Intent(context, PidProviderActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pidProviderViewModel = PIDProviderViewModel(this)

        pidProviderViewModel.completeAuthFlow()

        pidProviderViewModel.credentialLiveData.observe(this) { _ ->
            PidSdkCompleteCallbackManager.invokeOnError(PIDProviderException("this device is without nfc, you cannot continue with this operation"))
            this@PidProviderActivity.finish()
        }
    }

}