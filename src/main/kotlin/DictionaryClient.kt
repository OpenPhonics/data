package com.openphonics.data

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictionaryClient {
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
            val response = apiService.getWord(word).execute()
            if (response.isSuccessful) {
                response.body()?.get(0)?.phonetic
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
    fun getWord(@Path("word") word: String): Call<List<Word>>
}
data class Word(
    @SerializedName("phonetic") val phonetic: String?
)
