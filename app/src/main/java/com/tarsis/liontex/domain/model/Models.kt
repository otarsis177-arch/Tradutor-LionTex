package com.tarsis.liontex.domain.model

enum class Language(val code: String, val displayName: String) {
  AUTO("auto", "Detectar Idioma"),
  ENGLISH("en", "Inglês"),
  PORTUGUESE("pt", "Português"),
  SPANISH("es", "Espanhol");

  companion object {
    fun fromCode(code: String): Language {
      return entries.find { it.code.equals(code, ignoreCase = true) } ?: AUTO
    }
  }
}

data class WordBreakdown(
  val word: String,
  val translation: String,
  val partOfSpeech: String,
  val grammarNote: String = "",
  val phonetic: String = "",
  val exampleEn: String = "",
  val examplePt: String = ""
)

data class StudyBreakdown(
  val fullSentence: String,
  val fullTranslation: String,
  val words: List<WordBreakdown>,
  val grammarStructure: String,
  val tenseIdentified: String,
  val studyTip: String,
  val alternativeTranslations: List<String> = emptyList()
)

data class OcrBlock(
  val rawText: String,
  val correctedText: String,
  val confidence: Float,
  val hasWarnings: Boolean,
  val warningNote: String? = null
)

data class OcrResult(
  val fullRawText: String,
  val fullCorrectedText: String,
  val blocks: List<OcrBlock>,
  val confidenceScore: Float,
  val processingTimeMs: Long,
  val detectedLanguage: Language
)

data class TranslationResult(
  val originalText: String,
  val translatedText: String,
  val sourceLang: Language,
  val targetLang: Language,
  val detectedLang: Language,
  val confidenceScore: Float = 0.95f,
  val contextualExplanation: String = "",
  val timestamp: Long = System.currentTimeMillis()
)

data class HistoryItem(
  val id: Long = 0,
  val originalText: String,
  val translatedText: String,
  val sourceLangCode: String,
  val targetLangCode: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isFavorite: Boolean = false,
  val sourceMode: String = "text" // "text", "image", "screen"
)

data class FlashcardItem(
  val id: Long = 0,
  val frontText: String,
  val backText: String,
  val hint: String = "",
  val reviewCount: Int = 0,
  val lastReviewed: Long = 0L,
  val isMastered: Boolean = false
)
