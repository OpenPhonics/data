package com.openphonics.data

import com.openphonics.data.clients.*
import com.openphonics.data.database.*
import com.openphonics.data.utils.addHeader
import com.openphonics.data.utils.readCsv
import com.openphonics.data.utils.readWords
import com.openphonics.data.utils.saveWordsLocally

data class Word(
    val language: String,
    val word: String,
    val phonic: String?,
    val sound: String?,
    val id: Int
)
data class LanguageInformation(
    val code: String,
    val region: String,
    val name: String,
    val country: String
)

const val INPUT_LANGUAGE = "en"
const val WORD_TXT_PATH = "/Users/advaitvedant/Documents/data/src/main/kotlin/Resources/words.txt"
const val WORD_CSV_PATH = "/Users/advaitvedant/Documents/data/src/main/kotlin/Resources/words.csv"
const val TEMP_PATH = "/Users/advaitvedant/Documents/data/src/main/kotlin/word.mp3"
const val SOUND_URL = "https://StorageClient.googleapis.com/"
const val BUCKET_NAME = "openphonics_language_data"
const val SOUND_FILE_ENDING = "mp3"
val HEADER = listOf("language", "word", "phonic", "sound", "id")
val ENGLISH = LanguageInformation("en", "en-US", "English", "US")
val TAMIL = LanguageInformation("ta", "ta-IN", "Tamil", "IN")
suspend fun main() {
//    //creates the bucket
//    StorageClient.createBucket(BUCKET_NAME)
//
//    //clears and adds a header to the csv
//    addHeader(WORD_CSV_PATH, HEADER)
//
//    //creates the languages locally
//    createLanguageLocally(ENGLISH.code, ENGLISH.region)
//    createLanguageLocally(TAMIL.code, TAMIL.region)
//
    //adds the languages to the remote database
    addLanguageToRemoteDatabase(ENGLISH.code, ENGLISH.name, ENGLISH.country)
    addLanguageToRemoteDatabase(TAMIL.code, TAMIL.name, TAMIL.country)

    //add Tamil to English Course to database
    addCourseToRemoteDatabase(source = TAMIL, target = ENGLISH)

}
suspend fun addLanguageToRemoteDatabase(code: String, name: String, country: String) {
    val languageId = RemoteDatabaseClient.createLanguage(LanguageCreate(code, name, country))
        ?: throw Exception("Language not added to remote database")
    val wordsFromLocalDatabase = readCsv(WORD_CSV_PATH).filter { it.language == code }
    for (word in wordsFromLocalDatabase) {
        RemoteDatabaseClient.createWord(WordCreate(word.word, word.phonic, languageId))
            ?: throw Exception("Word not added to remote database ${word.word}")
    }
}
suspend fun addCourseToRemoteDatabase(source: LanguageInformation, target: LanguageInformation){
    val sourceLanguage = RemoteDatabaseClient.getLanguageCode(source.code)
        ?: throw Exception("Source language not found")
    val targetLanguage = RemoteDatabaseClient.getLanguageCode(target.code)
        ?: throw Exception("Target language not found")
    val course = RemoteDatabaseClient.createCourse(CourseCreate(sourceLanguage.id, targetLanguage.id))
        ?: throw Exception("Course not added to remote database")
    val sourceWords = RemoteDatabaseClient.getWordsByLanguage(sourceLanguage.id)
    val targetWords = RemoteDatabaseClient.getWordsByLanguage(targetLanguage.id)
    for ((index, _) in sourceWords.withIndex()) {
        RemoteDatabaseClient.createCourseWords(CourseWordCreate(sourceWords[index].id, targetWords[index].id, course))
            ?: throw Exception("Course word not added to remote database")
    }
}
suspend fun createLanguageLocally(
    language: String,
    region: String,
) {
    val inputWords = readWords(WORD_TXT_PATH)
    //Prepares clients for translation, text to speech
    val translate = TranslateClient(INPUT_LANGUAGE, language)
    val tts = TextToSpeechClient(region)

    //list of words added to local database
    val words: MutableList<Word> = mutableListOf()

    for ((index, text) in inputWords.withIndex()) {
        //Gets necessary data for word
        var phonetic: String? = null
        var word: String = text
        if (language == INPUT_LANGUAGE) {
            phonetic = PhonicClient.getPhonetic(text)
        } else {
            word = translate.translate(text)
        }

        //generates audio and saves it locally
        tts.speech(word, TEMP_PATH)

        //saves audio to remote database
        val sound = StorageClient.uploadMp3(TEMP_PATH, BUCKET_NAME, "$language/$word.$SOUND_FILE_ENDING")

        //adds word to list to be added to local database later
        words.add(
            Word(
                language = language,
                word = word,
                phonic = phonetic,
                sound = "$SOUND_URL$BUCKET_NAME/$sound",
                index
            )
        )
    }
    saveWordsLocally(WORD_CSV_PATH, words)
}