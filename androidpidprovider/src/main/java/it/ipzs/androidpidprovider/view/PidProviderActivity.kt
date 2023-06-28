@file:Suppress("UNUSED_VARIABLE")

package it.ipzs.androidpidprovider.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import it.ipzs.androidpidprovider.constant.UrlConstant
import it.ipzs.androidpidprovider.databinding.ActivityPidProviderBinding
import it.ipzs.androidpidprovider.utils.PidSdkCallbackManager
import it.ipzs.androidpidprovider.storage.PidProviderSDKShared
import it.ipzs.androidpidprovider.utils.PidProviderConfigUtils
import it.ipzs.androidpidprovider.utils.UrlUtils
import it.ipzs.androidpidprovider.view.base.ABaseActivity

internal class PidProviderActivity : ABaseActivity<ActivityPidProviderBinding>() {

    private lateinit var pidProviderViewModel: PIDProviderViewModel

    override fun setBinding(): ActivityPidProviderBinding =
        ActivityPidProviderBinding.inflate(layoutInflater)

    private val observerAuthFlow = Observer<String> { requestUri ->
        val clientId = PidProviderSDKShared.getInstance(applicationContext).getClientId()
        val url = UrlUtils.buildAuthorizeUrl(applicationContext, clientId, requestUri)
        configureWebView(url, requestUri)
    }

    companion object {

        fun start(context: Context){
            val intent = Intent(context, PidProviderActivity::class.java)
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pidProviderViewModel = PIDProviderViewModel(this)
        pidProviderViewModel.startAuthFlow()

        pidProviderViewModel.requestUriLiveData.observe(
            this,
            observerAuthFlow
        )

        pidProviderViewModel.credentialLiveData.observe(this) { credential ->
            PidSdkCallbackManager.invokeOnComplete(credential)
            this@PidProviderActivity.finish()
        }
    }

    private fun configureWebView(url: String, requestUri: String) {
        val walletUri = PidProviderConfigUtils.getWalletUri(this)
        binding.webViewPid.apply {
            webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val currentUri = request?.url
                    if (currentUri.toString().contains(walletUri, true)) {
                        val code = currentUri?.getQueryParameters(UrlConstant.CODE_PARAM)?.first().orEmpty()
                        val state = currentUri?.getQueryParameters(UrlConstant.STATE_PARAM)?.first()
                        pidProviderViewModel.getTokenAndCredential(code, requestUri)
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressPidProvider.isVisible = false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    PidSdkCallbackManager.invokeOnError(Exception(error.toString()))
                }
            }
            loadUrl(url)
        }
    }

}