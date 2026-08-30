package com.tarsis.liontex.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class LionTexTtsManager(context: Context) {

  private var textToSpeech: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _currentSpeed = MutableStateFlow(1.0f)
  val currentSpeed: StateFlow<Float> = _currentSpeed.asStateFlow()

  init {
    textToSpeech = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        val result = textToSpeech?.setLanguage(Locale.US)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.w("LionTexTts", "US English TTS language not supported directly, falling back to default.")
        }
        isInitialized = true
        setupProgressListener()
      } else {
        Log.e("LionTexTts", "TextToSpeech initialization failed.")
      }
    }
  }

  private fun setupProgressListener() {
    textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(utteranceId: String?) {
        _isSpeaking.value = true
      }

      override fun onDone(utteranceId: String?) {
        _isSpeaking.value = false
      }

      override fun onError(utteranceId: String?) {
        _isSpeaking.value = false
      }
    })
  }

  fun setSpeechRate(rate: Float) {
    _currentSpeed.value = rate.coerceIn(0.5f, 2.0f)
    textToSpeech?.setSpeechRate(_currentSpeed.value)
  }

  fun speak(text: String, language: Locale = Locale.US) {
    if (!isInitialized || text.isBlank()) return

    textToSpeech?.language = language
    textToSpeech?.setSpeechRate(_currentSpeed.value)

    val utteranceId = "LionTex_TTS_${System.currentTimeMillis()}"
    textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
  }

  fun stop() {
    textToSpeech?.stop()
    _isSpeaking.value = false
  }

  fun shutdown() {
    textToSpeech?.stop()
    textToSpeech?.shutdown()
    textToSpeech = null
    isInitialized = false
  }
}
