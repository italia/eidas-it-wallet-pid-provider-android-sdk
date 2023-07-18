package it.ipzs.androidpidprovider.view

import android.content.Context
import androidx.lifecycle.viewModelScope
import it.ipzs.androidpidprovider.external.PidCredential
import it.ipzs.androidpidprovider.facade.PidProviderFacade
import it.ipzs.androidpidprovider.network.datasource.PidProviderDataSource
import it.ipzs.androidpidprovider.network.datasource.PidProviderDataSourceImpl
import it.ipzs.androidpidprovider.utils.SingleLiveData
import it.ipzs.androidpidprovider.view.base.ABaseViewModel
import it.ipzs.cieidsdk.data.PidCieData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PIDProviderViewModel(context: Context) : ABaseViewModel() {

    private var pidProviderFacade: PidProviderFacade = PidProviderFacade(context)

    val credentialLiveData: SingleLiveData<PidCredential> = SingleLiveData()

    fun completeAuthFlow(pidCieData: PidCieData?) {
        viewModelScope.launch(startExceptionHandler) {
            withContext(Dispatchers.IO) {
                credentialLiveData.postValue(pidProviderFacade.getCredential(pidCieData))
            }
        }
    }
}