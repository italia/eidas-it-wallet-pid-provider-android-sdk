package it.ipzs.androidpidprovider.view.cie

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import it.ipzs.androidpidprovider.databinding.FragmentCieAuthBinding
import it.ipzs.androidpidprovider.utils.PidProviderConfigUtils
import it.ipzs.androidpidprovider.view.PidProviderActivity
import it.ipzs.androidpidprovider.view.base.ABaseFragment
import it.ipzs.cieidsdk.common.CieIDSdk

internal class CieAuthFragment: ABaseFragment<FragmentCieAuthBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btContinue.setOnClickListener {
            applyPin()
        }
    }

    override fun setBinding(): FragmentCieAuthBinding {
        return FragmentCieAuthBinding.inflate(layoutInflater)
    }

    private fun applyPin() {
        try {
            CieIDSdk.pin = binding.editTextPin.text.toString()
            startNFC()
        } catch (exception: Exception) {
            showErrorAlert(exception.localizedMessage)
        }
    }

    private fun showErrorAlert(errorMessage: String?) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(errorMessage)
            .setPositiveButton("Ok") { _, _ -> }
        builder.show()
    }

    private fun startNFC() {
        NfcReaderDialog.show(requireContext())
        val pidProviderActivity = activity as PidProviderActivity
        CieIDSdk.start(pidProviderActivity, pidProviderActivity)
        CieIDSdk.startNFCListening(pidProviderActivity)
        CieIDSdk.enableLog = PidProviderConfigUtils.isLogEnabled(requireContext())
    }

}