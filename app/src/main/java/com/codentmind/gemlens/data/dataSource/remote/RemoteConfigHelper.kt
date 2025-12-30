package com.codentmind.gemlens.data.dataSource.remote

import com.codentmind.gemlens.R
import com.codentmind.gemlens.utils.AnalyticsHelper.logEvents
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

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
    fun fetchConfigData(key: String, onComplete: (String) -> Unit) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logEvents("RemoteConfig", "Fetch successful")
                }
                onComplete.invoke(remoteConfig.getString(key))
            }
    }
}