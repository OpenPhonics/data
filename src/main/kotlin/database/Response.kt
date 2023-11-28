package com.openphonics.data.database

import kotlinx.serialization.Serializable
interface Response {
    val status: State
    val message: String
}
enum class State {
    SUCCESS, NOT_FOUND, FAILED, UNAUTHORIZED
}

@Serializable
data class IdResponse(
    override val status: State,
    override val message: String,
    val id: Int? = null
) : Response

@Serializable
data class DataResponse<T>(
    override val status: State,
    override val message: String,
    val data: List<T> = emptyList()
) : Response