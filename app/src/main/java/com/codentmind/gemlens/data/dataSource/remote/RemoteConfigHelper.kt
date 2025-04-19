package com.codentmind.gemlens.data.dataSource.remote

import android.app.Activity
import com.codentmind.gemlens.R
import com.codentmind.gemlens.utils.Constant.CONFIG_API_KEY
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

/**
 * @Details :FirebaseRemoteConfig
 * @Author Roshan Bhagat
 */
class RemoteConfigHelper {
    private val remoteConfig by lazy { Firebase.remoteConfig }

    init {
        setupConfig()
    }

    /**
     * Setup config
     *
     */
    private fun setupConfig() {
        // Set config settings
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0 // 1 hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }


    /**
     * Fetches configuration data from Firebase Remote Config and invokes the onComplete callback with the API key.
     * @param activity The activity used for adding completion listeners.
     * @param onComplete Callback invoked with the API key string upon completion.
     */
    fun fetchConfigData(activity: Activity, onComplete: (String) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {

                }
                onComplete.invoke(remoteConfig.getString(CONFIG_API_KEY))
            }
    }


}