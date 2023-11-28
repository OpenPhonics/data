package com.openphonics.data.clients

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.awaitResponse
interface PhonicService {
    @GET("{word}")
    fun getPhonetic(@Path("word") word: String): Call<List<Phonetic>>
}
interface PhonicRepository {
    suspend fun getPhonetic(word: String): String?
}
data class Phonetic(
    @SerializedName("phonetic") val phonetic: String?
)

object PhonicClient : PhonicRepository {

    private val BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/"

    private val phonicService: PhonicService

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        phonicService = retrofit.create(PhonicService::class.java)
    }

    override suspend fun getPhonetic(word: String): String? {
        return try {
            val response = phonicService.getPhonetic(word).awaitResponse()
            if (response.isSuccessful) {
                val phonetic = response.body()?.get(0)?.phonetic
                phonetic?.substring(1, phonetic.length - 1)
            } else {
                null
            }
        } catch (exception: Exception) {
            null
        }
    }
}
