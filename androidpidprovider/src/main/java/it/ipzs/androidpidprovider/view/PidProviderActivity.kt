@file:Suppress("UNUSED_VARIABLE")

package it.ipzs.androidpidprovider.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import it.ipzs.androidpidprovider.databinding.ActivityPidProviderBinding
import it.ipzs.androidpidprovider.utils.*
import it.ipzs.androidpidprovider.view.base.ABaseActivity
import it.ipzs.androidpidprovider.view.cie.NfcReaderDialog
import it.ipzs.cieidsdk.common.Callback
import it.ipzs.cieidsdk.common.CieIDSdk
import it.ipzs.cieidsdk.data.PidCieData
import it.ipzs.cieidsdk.event.Event

internal class PidProviderActivity : ABaseActivity<ActivityPidProviderBinding>(), Callback {

    private lateinit var pidProviderViewModel: PIDProviderViewModel

    private val serviceProviderUrl = "https://sp.collaudo.idserver.servizicie.interno.gov.it/"

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

        configureWebView()

        pidProviderViewModel.credentialLiveData.observe(this) { credential ->
            PidSdkCompleteCallbackManager.invokeOnComplete(credential)
            this@PidProviderActivity.finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        val walletUri = PidProviderConfigUtils.getWalletUri(this)
        binding.webViewPid.apply {
            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val currentUri = request?.url
                    if (currentUri.toString().contains("idp/login/livello3?opId")) {
                        binding.lottieView.isVisible = false
                        binding.fragmentContainerView.isVisible = true
                        CieIDSdk.setUrl(currentUri.toString())
                        return true
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (url == serviceProviderUrl) {
                        view?.evaluateJavascript("document.getElementsByName(\"f3\")[0].submit()", null)
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    PidSdkStartCallbackManager.invokeOnError(Exception(error.toString()))
                }
            }
            settings.javaScriptEnabled = true
            loadUrl(serviceProviderUrl)
        }
    }

    override fun onResume() {
        super.onResume()
        CieIDSdk.startNFCListening(this)
    }

    override fun onPause() {
        super.onPause()
        CieIDSdk.stopNFCListening(this)
    }

    override fun onEvent(event: Event) {
        LogHelper.d(this, "onEvent", event.toString())
    }

    override fun onError(error: Throwable) {
        LogHelper.e(this, "onEvent", error.message.toString())
        runOnUiThread {
            NfcReaderDialog.hide()
        }
        throw error
    }

    override fun onSuccess(url: String, pinCieData: PidCieData?) {
        runOnUiThread {
            NfcReaderDialog.hide()
        }
        pidProviderViewModel.completeAuthFlow(pinCieData)
    }

}