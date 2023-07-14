package it.ipzs.androidpidprovider.view.base

import androidx.lifecycle.ViewModel
import it.ipzs.androidpidprovider.utils.PidSdkStartCallbackManager
import kotlinx.coroutines.CoroutineExceptionHandler

internal open class ABaseViewModel : ViewModel() {

    var startExceptionHandler = CoroutineExceptionHandler { _, exception ->
        PidSdkStartCallbackManager.invokeOnError(exception)
    }

}