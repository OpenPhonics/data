package com.openphonics.data.database

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RemoteDatabaseService {
    @POST("languages")
    fun createLanguage(@Body languageCreate: LanguageCreate): Call<IdResponse>
    @POST("words")
    fun createWord(@Body wordCreate: WordCreate): Call<IdResponse>
    @POST("courses")
    fun createCourse(@Body courseCreate: CourseCreate): Call<IdResponse>
    @POST("coursewords")
    fun createCourseWords(@Body courseWordsCreate: CourseWordCreate): Call<IdResponse>
    @GET("languages/code/{code}")
    fun getLanguageCode(@Path("code") code: String): Call<DataResponse<LanguageBase>>
    @GET("words/language/{languageId}")
    fun getWordsByLanguage(@Path("languageId") languageId: Int): Call<DataResponse<WordBase>>
}
interface RemoteDatabaseRepository {
    suspend fun createLanguage(languageCreate: LanguageCreate): Int?
    suspend fun createWord(wordCreate: WordCreate): Int?
    suspend fun createCourse(courseCreate: CourseCreate): Int?
    suspend fun createCourseWords(courseWordsCreate: CourseWordCreate): Int?
    suspend fun getLanguageCode(code: String): LanguageBase?
    suspend fun getWordsByLanguage(languageId: Int): List<WordBase>


}
object RemoteDatabaseClient : RemoteDatabaseRepository {

    private val BASE_URL = "http://localhost:8080/"

    private val database: RemoteDatabaseService

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        database = retrofit.create(RemoteDatabaseService::class.java)
    }

    override suspend fun createLanguage(languageCreate: LanguageCreate): Int? {
        return try {
            val response = database.createLanguage(languageCreate).awaitResponse()
            val body = response.body()
            body?.id ?: throw Exception("BAD Language request ${body?.message}")
        } catch (exception: Exception) {
            throw Exception("BAD Language request")
        }
    }

    override suspend fun createWord(wordCreate: WordCreate): Int? {
        return try {
            val response = database.createWord(wordCreate).awaitResponse()
            val body = response.body()
            body?.id ?: throw Exception("BAD Word request ${body?.message}")
        } catch (exception: Exception) {
            throw Exception("BAD Word request")
        }
    }

    override suspend fun createCourse(courseCreate: CourseCreate): Int? {
        return try {
            val response = database.createCourse(courseCreate).awaitResponse()
            val body = response.body()
            body?.id ?: throw Exception("BAD Word request ${body?.message}")
        } catch (exception: Exception) {
            throw Exception("BAD Course request")
        }
    }

    override suspend fun createCourseWords(courseWordsCreate: CourseWordCreate): Int? {
        return try {
            val response = database.createCourseWords(courseWordsCreate).awaitResponse()
            val body = response.body()
            body?.id ?: throw Exception("BAD Word request ${body?.message}")
        } catch (exception: Exception) {
            throw Exception("BAD Course Word request")
        }
    }

    override suspend fun getLanguageCode(code: String): LanguageBase? {
        return try {
            val response = database.getLanguageCode(code).awaitResponse()
            response.body()?.data?.get(0)
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun getWordsByLanguage(languageId: Int): List<WordBase> {
        return try {
            val response = database.getWordsByLanguage(languageId).awaitResponse()
            response.body()?.data ?: emptyList()
        } catch (exception: Exception) {
            emptyList()
        }
    }

}