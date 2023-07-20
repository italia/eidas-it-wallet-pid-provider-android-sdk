@file:Suppress("PrivatePropertyName")

package it.ipzs.androidpidproviderdemo

import android.app.Application
import it.ipzs.androidpidprovider.external.PidProviderConfig
import it.ipzs.androidpidprovider.external.PidProviderSdk

class PidProviderDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeSDK()
    }

    private fun initializeSDK() {
        val walletInstance = "eyJhbGciOiJub25lIn0.eyJyZXNwb25zZV90eXBlc19zdXBwb3J0ZWQiOlsidnBfdG9rZW4iXSwic3ViIjoidmJlWEprc000NXhwaHRBTm5DaUc2bUN5dVU0amZHTnpvcEd1S3ZvZ2c5YyIsImxvZ29fdXJpIjoiaHR0cHM6Ly93YWxsZXQtcHJvdmlkZXIuZXhhbXBsZS5vcmcvbG9nby5zdmdcIiIsImlzcyI6Imh0dHBzOi8vd2FsbGV0LXByb3ZpZGVyLmV4YW1wbGUub3JnIiwidHlwZSI6IldhbGxldEluc3RhbmNlQXR0ZXN0YXRpb24iLCJwcmVzZW50YXRpb25fZGVmaW5pdGlvbl91cmlfc3VwcG9ydGVkIjpmYWxzZSwidnBfZm9ybWF0c19zdXBwb3J0ZWQiOnsiand0VmNKc29uIjp7ImFsZ1ZhbHVlc1N1cHBvcnRlZCI6WyJFUzI1NiJdfSwiand0VnBKc29uIjp7ImFsZ1ZhbHVlc1N1cHBvcnRlZCI6WyJFUzI1NiJdfX0sImF1dGhvcml6YXRpb25fZW5kcG9pbnQiOiJldWRpdzoiLCJhc2MiOiJodHRwczovL3dhbGxldC1wcm92aWRlci5leGFtcGxlLm9yZy9Mb0EvYmFzaWMiLCJjbmYiOnsiandrIjp7ImNydiI6IlAtMjU2Iiwia2lkIjoidmJlWEprc000NXhwaHRBTm5DaUc2bUN5dVU0amZHTnpvcEd1S3ZvZ2c5YyIsImt0eSI6IkVDIiwieCI6IjRITnB0SS14cjJwanlSSktHTW56NFdtZG5RRF91SlNxNFI5NU5qOThiNDQiLCJ5IjoiTElablNCMzl2RkpoWWdTM2s3alhFNHIzLUNvR0ZRd1p0UEJJUnFwTmxyZyJ9fSwidG9zX3VyaSI6Imh0dHBzOi8vd2FsbGV0LXByb3ZpZGVyLmV4YW1wbGUub3JnL2luZm9fcG9saWN5IiwiZXhwIjoiMTY5NDMzMjgwMDAwMCIsImlhdCI6IjE2ODk4MDYyMDQyNzQiLCJyZXF1ZXN0X29iamVjdF9zaWduaW5nX2FsZ192YWx1ZXNfc3VwcG9ydGVkIjpbIkVTMjU2Il0sInBvbGljeV91cmkiOiJodHRwczovL3dhbGxldC1wcm92aWRlci5leGFtcGxlLm9yZy9wcml2YWN5X3BvbGljeSJ9."

        val pidProviderConfig = PidProviderConfig
            .Builder()
            .baseUrl("https://localhost:8080/") // TODO: Update base url
            .walletInstance(walletInstance)
            .walletUri("https://www.google.com")
            .logEnabled(true)
            .build()
        PidProviderSdk.initialize(
            applicationContext,
            pidProviderConfig = pidProviderConfig
        )
    }

}