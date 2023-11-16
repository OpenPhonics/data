package com.openphonics.data.clients

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DictionaryClient {
    private val baseUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    suspend fun phonetic(word: String): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getPhonetic(word).execute()
            if (response.isSuccessful) {
                val phonetic = response.body()?.get(0)?.phonetic
                phonetic?.substring(1, phonetic.length - 1)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
interface ApiService {
    @GET("{word}")
    fun getPhonetic(@Path("word") word: String): Call<List<Phonetic>>
}
data class Phonetic(
    @SerializedName("phonetic") val phonetic: String?
)
