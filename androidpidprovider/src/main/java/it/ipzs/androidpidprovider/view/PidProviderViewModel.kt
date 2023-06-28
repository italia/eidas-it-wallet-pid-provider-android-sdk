package it.ipzs.androidpidprovider.view

import android.content.Context
import androidx.lifecycle.viewModelScope
import it.ipzs.androidpidprovider.external.PidCredential
import it.ipzs.androidpidprovider.facade.PidProviderFacade
import it.ipzs.androidpidprovider.network.datasource.PidProviderDataSource
import it.ipzs.androidpidprovider.network.datasource.PidProviderDataSourceImpl
import it.ipzs.androidpidprovider.utils.SingleLiveData
import it.ipzs.androidpidprovider.view.base.ABaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PIDProviderViewModel(context: Context) : ABaseViewModel() {

    private var dataSourceImpl: PidProviderDataSource = PidProviderDataSourceImpl(context)
    private var pidProviderFacade: PidProviderFacade = PidProviderFacade(context, dataSourceImpl)

    val requestUriLiveData: SingleLiveData<String> = SingleLiveData()
    val credentialLiveData: SingleLiveData<PidCredential> = SingleLiveData()

    fun startAuthFlow() {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                val requestUri = pidProviderFacade.startAuthFlow()
                withContext(Dispatchers.Main) {
                    requestUriLiveData.value = requestUri
                }
            }
        }
    }

    fun getTokenAndCredential(code: String, requestUri: String) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                val credential = pidProviderFacade.getTokenAndCredential(code, requestUri)
                withContext(Dispatchers.Main) {
                    credentialLiveData.value = credential
                }
            }
        }
    }

}