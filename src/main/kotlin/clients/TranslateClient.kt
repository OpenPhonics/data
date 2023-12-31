package com.openphonics.data.clients

import com.google.cloud.translate.Language
import com.google.cloud.translate.Translate
import com.google.cloud.translate.Translate.TranslateOption
import com.google.cloud.translate.Translate.TranslateOption.sourceLanguage
import com.google.cloud.translate.Translate.TranslateOption.targetLanguage
import com.google.cloud.translate.TranslateOptions

class TranslateClient(
    sourceLanguage: String,
    targetLanguage: String,
) {
    private val sourceLanguage: TranslateOption = sourceLanguage(sourceLanguage)
    private val targetLanguage: TranslateOption = targetLanguage(targetLanguage)
    private val translate: Translate = TranslateOptions.getDefaultInstance().getService()

    fun translate(text: String): String {
        val translation = translate.translate(text, sourceLanguage, targetLanguage);
        return translation.translatedText
    }
}