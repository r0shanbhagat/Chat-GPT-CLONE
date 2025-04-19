package com.codentmind.gemlens.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsHelper {

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun logButtonClick(buttonName: String) {
        val bundle = Bundle().apply {
            putString("button_name", buttonName)
        }
        firebaseAnalytics?.logEvent("button_click", bundle)
    }

    fun logEvent(eventName: String, params: Bundle = Bundle()) {
        firebaseAnalytics?.logEvent(eventName, params)
    }
}

