package com.openphonics.data.utils

import com.openphonics.data.Word
import java.io.*

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
fun saveWordsLocally(filePath: String, words: List<Word>) {

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
//    val lines = File(path).readLines()
    return listOf("the",
    "of",
    "and",
    "to",
    "a",
    "in",
    "that",
    "I",
    "was",
    "he")
//    return lines.flatMap { it.split("\\s+".toRegex()) }
}

fun readCsv(file: String): List<Word> {
    val inputStream = File(file).inputStream()
    val reader = inputStream.bufferedReader()
    val header = reader.readLine()
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val (language, word, phonic, sound, id) = it.split(',', ignoreCase = false, limit = 5)
            Word(language, word, phonic, sound, id.toInt())
        }.toList()
}