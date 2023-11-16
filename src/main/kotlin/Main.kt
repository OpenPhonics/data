package com.openphonics.data

import com.openphonics.data.clients.DictionaryClient
import com.openphonics.data.clients.StorageClient
import com.openphonics.data.clients.TextToSpeechClient
import com.openphonics.data.clients.TranslateClient
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

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
const val SOUND_URL = "https://storage.googleapis.com/"
suspend fun main() {
    val input = readWords(INPUT_PATH)
    val en = createLanguage("en", "en-US", "openphonics_language_data", input)
    val ta = createLanguage("ta", "ta-IN", "openphonics_language_data", input)
    addHeader(OUTPUT_PATH, listOf("language", "word", "phonic", "sound", "id"))
    addWords(OUTPUT_PATH, en)
    addWords(OUTPUT_PATH, ta)
}

fun addHeader(filePath: String, columns: List<String>) {
    try {
        // Create a BufferedWriter
        val writer = BufferedWriter(FileWriter(filePath))
        var header = ""
        columns.forEach {column ->
            header += "$column,"
        }
        writer.write("$header\n")
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
fun addWords(filePath: String, words: List<Word>) {

    try {
        // Create a BufferedWriter
        val writer = BufferedWriter(FileWriter(filePath, true))

        // Write each object to the CSV file
        words.forEach { word ->
            writer.write("${word.language},${word.word},${word.phonic},${word.sound},${word.id}\n")
        }

        // Close the writer
        writer.close()

        println("CSV file written successfully.")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
fun readWords(path: String): List<String>{
    val lines = File(path).readLines()
    return lines.flatMap { it.split("\\s+".toRegex()) }
}
fun toMp3Url(bucket: String, sound: String): String{
    return "$SOUND_URL$bucket/$sound"
}
suspend fun createLanguage(language: String, region: String, bucketName: String, input: List<String>): List<Word>{
    val dictionary = DictionaryClient()
    val storage = StorageClient()
    val translate = TranslateClient(INPUT_LANGUAGE, language)
    val tts = TextToSpeechClient(region)
    val bucket = storage.create(bucketName)
    val output: MutableList<Word> = mutableListOf()
    for ((index, text) in input.withIndex()){
        var phonetic: String? = null
        var word: String = text
        if (language == INPUT_LANGUAGE){
            phonetic = dictionary.phonetic(text)
        } else {
            word = translate.translate(text)
        }
        tts.speech(word, TEMP_PATH)
        val sound = storage.upload(TEMP_PATH, bucket, "$language/$word.mp3")
        output.add(Word(language, word, phonetic, toMp3Url(bucket, sound), index))
    }
    return output
}