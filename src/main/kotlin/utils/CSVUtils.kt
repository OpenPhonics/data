package com.openphonics.data.utils

import com.openphonics.data.Word
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

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
