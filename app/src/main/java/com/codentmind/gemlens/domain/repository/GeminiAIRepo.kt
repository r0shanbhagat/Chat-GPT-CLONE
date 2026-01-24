package com.codentmind.gemlens.domain.repository


import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.SafetySetting

interface GeminiAIRepo {

    /**
     * Provides a default configuration for text generation.
     *
     * @return [GenerationConfig]
     */
    fun provideConfig(): GenerationConfig

    /**
     * Provides a list of safety settings for the generative model.
     *
     * @return List of [SafetySetting]
     */
    fun provideSafetySetting(): List<SafetySetting>

    /**
     * Returns a generative model with the specified configurations.
     *
     * The name of the model to be used (e.g., "gemini-pro").
     * @return A configured [GenerativeModel] instance.
     */
    fun getGenerativeModel(
    ): GenerativeModel

}