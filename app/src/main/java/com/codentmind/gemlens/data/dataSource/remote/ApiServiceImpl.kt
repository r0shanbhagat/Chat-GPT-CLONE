package com.codentmind.gemlens.data.dataSource.remote

import io.ktor.client.HttpClient


/**
 * @Details :ApiServiceImpl
 * @Author Roshan Bhagat
 */
class ApiServiceImpl(private val client: HttpClient) : ApiService {
    companion object {
        const val PATH = "api/v1/"
    }


}