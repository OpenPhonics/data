package com.openphonics.data

import com.openphonics.data.clients.DictionaryClient
import com.openphonics.data.clients.StorageClient
import com.openphonics.data.clients.TextToSpeechClient
import com.openphonics.data.clients.TranslateClient
import com.openphonics.data.utils.addHeader
import com.openphonics.data.utils.addWords
import com.openphonics.data.utils.readWords

data class Word(
    val language: String,
    val word: String,
    val phonic: String?,
    val sound: String?,
    val id: Int
)
const val INPUT_LANGUAGE = "en"
const val INPUT_PATH = "/Users/advaitvedant/Documents/data/src/main/kotlin/Resources/words.txt"
const val OUTPUT_PATH = "/Users/advaitvedant/Documents/data/src/main/kotlin/Resources/words.csv"
const val TEMP_PATH = "/Users/advaitvedant/Documents/data/src/main/kotlin/word.mp3"
const val SOUND_URL = "https://StorageClient.googleapis.com/"
const val BUCKET_NAME = "openphonics_language_data"
val HEADER = listOf("language", "word", "phonic", "sound", "id")
suspend fun main() {
    //gets all the words
    val input = readWords(INPUT_PATH)
    //creates the bucket
    val bucket = StorageClient.createBucket(BUCKET_NAME)
    //creates and saves the languages
    createEnglishToTamil(bucket, input)
}
suspend fun createEnglishToTamil(bucket: String, input: List<String>){
    //creates the languages
    val en = createLanguage("en", "en-US", bucket, input)
    val ta = createLanguage("ta", "ta-IN", bucket, input)
    //adds a header to the csv
    addHeader(OUTPUT_PATH, HEADER)
    //saves the languages to the csv
    addWords(OUTPUT_PATH, en)
    addWords(OUTPUT_PATH, ta)
}


suspend fun createLanguage(
    language: String,
    region: String,
    bucketName: String,
    input: List<String>,
): List<Word>{
    val translate = TranslateClient(INPUT_LANGUAGE, language)
    val tts = TextToSpeechClient(region)
    val output: MutableList<Word> = mutableListOf()
    for ((index, text) in input.withIndex()){
        var phonetic: String? = null
        var word: String = text
        if (language == INPUT_LANGUAGE){
            phonetic = DictionaryClient.phonetic(text)
        } else {
            word = translate.translate(text)
        }
        tts.speech(word, TEMP_PATH)
        val sound = StorageClient.uploadMp3(TEMP_PATH, bucketName, "$language/$word.mp3")
        output.add(Word(language, word, phonetic, "$SOUND_URL$bucketName/$sound", index))
    }
    return output
}