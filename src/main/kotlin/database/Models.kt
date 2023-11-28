package com.openphonics.data.database

import kotlinx.serialization.Serializable

@Serializable
data class LanguageCreate(
    val languageCode: String,
    val languageName: String,
    val countryCode: String
)
@Serializable
data class LanguageBase(
    val id: Int,
    val languageCode: String,
    val languageName: String,
    val countryCode: String
)
@Serializable
data class WordBase(
    val id: Int,
    val word: String,
    val phonic: String?,
    val language: Int
)

@Serializable
data class WordCreate(
    val word: String,
    val phonic: String?,
    val language: Int
)
@Serializable
data class CourseWordCreate(
    val sourceWord: Int,
    val targetWord: Int,
    val course: Int
)

@Serializable
data class CourseCreate(
    val sourceLanguage: Int,
    val targetLanguage: Int
)