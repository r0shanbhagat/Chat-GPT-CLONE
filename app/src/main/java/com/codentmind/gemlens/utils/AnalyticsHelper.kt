package com.codentmind.gemlens.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsHelper {

    private var firebaseAnalytics: FirebaseAnalytics? = null

    /**
     * Initializes the Firebase Analytics instance.
     *
     * @param context The application context.
     */
    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    /**
     * Logs a screen view event to Firebase Analytics.
     *
     * @param screenName The name of the screen being viewed.
     */
    fun logScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
        firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    /** Logs a button click event to Firebase Analytics.
     * @param buttonName The name of the button clicked.
     */
    fun logButtonClick(buttonName: String) {
        val bundle = Bundle().apply {
            putString("button_name", buttonName)
        }
        firebaseAnalytics?.logEvent("button_click", bundle)
    }

    /**
     * Logs a custom event to Firebase Analytics.
     *
     * @param eventName The name of the event.
     * @param eventValue The value associated with the event.
     */
    fun logEvents(eventName: String, eventValue: String) {
        val params = Bundle().apply {
            putString("eventValue", eventValue)
        }
        firebaseAnalytics?.logEvent(eventName, params)
    }
}

