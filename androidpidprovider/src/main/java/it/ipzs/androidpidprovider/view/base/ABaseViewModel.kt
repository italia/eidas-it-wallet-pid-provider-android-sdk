package it.ipzs.androidpidprovider.view.base

import androidx.lifecycle.ViewModel
import it.ipzs.androidpidprovider.utils.PidSdkCallbackManager
import kotlinx.coroutines.CoroutineExceptionHandler

internal open class ABaseViewModel(): ViewModel() {

    var exceptionHandler = CoroutineExceptionHandler { _, exception ->
        PidSdkCallbackManager.invokeOnError(exception)
    }

}