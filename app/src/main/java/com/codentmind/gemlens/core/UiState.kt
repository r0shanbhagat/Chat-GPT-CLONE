package com.codentmind.gemlens.core

sealed class UiState(val data: Any? = null, val message: String? = null) {
    class Success<T>(data: T?) : UiState(data)
    class Loading : UiState()
    class Error<T>(data: T? = null, message: String?) : UiState(data, message)
}