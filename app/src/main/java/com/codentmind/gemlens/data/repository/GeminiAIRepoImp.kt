package com.codentmind.gemlens.data.repository

import com.codentmind.gemlens.domain.repository.GeminiAIRepo
import com.codentmind.gemlens.utils.Constant.AI_MODEL_NAME
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerationConfig
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.generationConfig

class GeminiAIRepoImpl : GeminiAIRepo {

    override fun provideConfig(): GenerationConfig {
        return generationConfig {
            temperature = 0.7f
        }
    }

    override fun provideSafetySetting(): List<SafetySetting> = listOf(
        SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.NONE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.NONE),
        SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.NONE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.NONE),
    )

    override fun getGenerativeModel(): GenerativeModel {
        return Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = AI_MODEL_NAME,
            generationConfig = provideConfig(),
            safetySettings = provideSafetySetting()
        )
    }
}
