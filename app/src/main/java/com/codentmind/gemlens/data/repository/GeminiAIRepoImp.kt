package com.codentmind.gemlens.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.codentmind.gemlens.GemLensApp
import com.codentmind.gemlens.domain.repository.GeminiAIRepo
import com.codentmind.gemlens.utils.Constant.AI_MODEL_NAME
import com.codentmind.gemlens.utils.datastore
import com.codentmind.gemlens.utils.getApiKey
import kotlinx.coroutines.runBlocking

class GeminiAIRepoImpl : GeminiAIRepo {

    override fun provideConfig(): GenerationConfig {
        return generationConfig {
            temperature = 0.7f
        }
    }

    override fun provideSafetySetting(): List<SafetySetting> = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
    )

    override fun getGenerativeModel(): GenerativeModel {
        val apiKey = runBlocking { GemLensApp.getInstance().datastore.getApiKey() }
        return getGenerativeModel(apiKey)
    }

    override fun getGenerativeModel(apiKey: String): GenerativeModel {
        return GenerativeModel(
            modelName = AI_MODEL_NAME,
            apiKey = apiKey,
            generationConfig = provideConfig(),
            safetySettings = provideSafetySetting()
        )
    }
}
