package com.codentmind.gemlens.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class NetworkUtil private constructor() {

    @OptIn(ExperimentalSerializationApi::class)
    val jsonDecoder by lazy {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            allowSpecialFloatingPointValues = true
        }
    }


    val jsonEncoder by lazy {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
            allowSpecialFloatingPointValues = true
        }
    }


    fun cleanup() {
        instance = null
    }


    companion object {

        @Volatile
        private var instance: NetworkUtil? = null

        @JvmStatic
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: NetworkUtil().also { instance = it }
        }


        inline fun <reified T> jsonToModel(jsonInString: String): T =
            getInstance().jsonDecoder.decodeFromString(jsonInString)


        inline fun <reified T> modelToJson(obj: T): String =
            getInstance().jsonEncoder.encodeToString(Json.serializersModule.serializer(), obj)
    }

}

