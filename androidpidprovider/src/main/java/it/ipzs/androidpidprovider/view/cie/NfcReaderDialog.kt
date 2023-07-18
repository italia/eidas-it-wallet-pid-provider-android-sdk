package it.ipzs.androidpidprovider.view.cie

import it.ipzs.androidpidprovider.databinding.DialogNfcReaderBinding
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import it.ipzs.androidpidprovider.R

internal class NfcReaderDialog(
    context: Context
): Dialog(context) {
    private lateinit var binding: DialogNfcReaderBinding
    private var helper: CIEAccessHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogNfcReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        helper = CIEAccessHelper(binding.lNcfReader)
        helper?.attachToView()

        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.lNcfReader.cancelButtonNfcCie.setOnClickListener {
            hide()
        }

        helper?.showCieScanView()
    }

    companion object {
        private var dialog: NfcReaderDialog? = null

        fun show(
            context: Context,
            isCancelable: Boolean = true
        ) {
            dialog = NfcReaderDialog(context).apply {
                setCanceledOnTouchOutside(false)
                setCancelable(isCancelable)
                show()
            }
        }

        fun hide(completion: () -> Unit = {}) {
            dialog?.helper?.dismissCieScanView()

            Handler(Looper.getMainLooper()).postDelayed({
                if (dialog?.isShowing == true) {
                    dialog?.dismiss()
                }

                dialog?.helper = null
                dialog = null
                completion.invoke()
            }, 500)
        }

        @Suppress("unused")
        fun setIsReading(context: Context) {
            dialog?.binding?.lNcfReader?.nfcCieTitle?.text = context.getString(R.string.cie_reading)
            dialog?.binding?.lNcfReader?.nfcCieDescription?.text = context.getString(R.string.cie_reading_desc)
        }

    }
}