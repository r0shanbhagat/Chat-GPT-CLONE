package com.codentmind.gemlens.utils

object Constant {
    const val AI_MODEL_NAME = "gemini-2.0-flash"
    const val FILE_PROVIDER = "com.codentmind.gemlens.provider"
    const val ROLE_USER = "user"
    const val ROLE_MODEL = "model"
    const val CONFIG_API_KEY = "apikey"
    const val TAG = "GemLens"

    class Analytics {
        companion object {
            const val SCREEN_CHAT = "Chat"
            const val SCREEN_SETTINGS = "Settings"
            const val SCREEN_API = "API"
            const val SCREEN_ABOUT = "About"
        }
    }
}