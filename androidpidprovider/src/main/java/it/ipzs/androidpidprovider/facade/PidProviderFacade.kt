@file:Suppress("SpellCheckingInspection")

package it.ipzs.androidpidprovider.facade

import android.content.Context
import it.ipzs.androidpidprovider.external.PidCredential
import it.ipzs.androidpidprovider.network.datasource.PidProviderDataSource

internal class PidProviderFacade(
    context: Context,
    dataSource: PidProviderDataSource
) {

    private val pkceFacade = PKCEFacade(context, dataSource)

    suspend fun startAuthFlow(): String {
        return pkceFacade.startPKCE()
    }

    suspend fun getTokenAndCredential(code: String, requestUri: String): PidCredential {
        return pkceFacade.getTokenAndCredential(code, requestUri)
    }

}