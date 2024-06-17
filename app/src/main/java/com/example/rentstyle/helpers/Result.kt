package com.example.rentstyle.helpers

import kotlin.Result

sealed class DataResult <out R> {
    data class Success<out T>(val data: T): DataResult<T>()
    data class Error(val error: String): DataResult<Nothing>()
    data object Loading: DataResult<Nothing>()
}

sealed class StatusResult {
    data class Success(val success: String) : StatusResult()
    data class Error(val error: String) : StatusResult()
    data object Loading : StatusResult()
}