package com.openphonics.data

import com.google.cloud.texttospeech.v1.*
import com.google.cloud.texttospeech.v1.TextToSpeechClient
import java.io.FileOutputStream

class TextToSpeechClient(
    private val language: String = "en-US",
    private val gender: SsmlVoiceGender = SsmlVoiceGender.NEUTRAL,
    private val textToSpeech: TextToSpeechClient = TextToSpeechClient.create(),
    private val audioConfig: AudioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build(),
    private val voice: VoiceSelectionParams = VoiceSelectionParams.newBuilder().setLanguageCode(language).setSsmlGender(gender).build()) {
    fun speech(text: String, audioLocation: String): String {
        val input = SynthesisInput.newBuilder().setText(text).build()
        val response = textToSpeech.synthesizeSpeech(input, voice, audioConfig);
        val audioContents = response.audioContent
        val out = FileOutputStream(audioLocation).buffered()
        out.write(audioContents.toByteArray())
        out.close()
        return audioLocation
    }
}