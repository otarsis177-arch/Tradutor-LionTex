package com.tarsis.liontex.data.ai

import android.graphics.Bitmap
import android.util.Base64
import com.tarsis.liontex.BuildConfig
import com.tarsis.liontex.domain.model.Language
import com.tarsis.liontex.domain.model.StudyBreakdown
import com.tarsis.liontex.domain.model.TranslationResult
import com.tarsis.liontex.domain.model.WordBreakdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

object GeminiTranslationService {

  private fun getApiKey(): String {
    return BuildConfig.GEMINI_API_KEY
  }

  fun isAiAvailable(): Boolean {
    val key = getApiKey()
    return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
  }

  /**
   * Traduz um texto usando o modelo Gemini 3.5 Flash com alta precisão contextual,
   * detecção de nuances e explicação idiomática.
   */
  suspend fun translateWithAi(
    text: String,
    sourceLanguage: Language = Language.AUTO,
    targetLanguage: Language = Language.PORTUGUESE
  ): Result<TranslationResult> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext Result.failure(IllegalStateException("Chave da API Gemini não configurada."))
    }

    val sourceDesc = if (sourceLanguage == Language.AUTO) "Detect the source language automatically" else "Source language is ${sourceLanguage.displayName}"
    val targetDesc = "Target language is ${targetLanguage.displayName}"

    val systemPrompt = """
      You are an expert polyglot linguist and master English/Portuguese translator specialized in natural, fluent, and accurate translations for learners and professionals.
      Translate the input text considering idiomatic expressions, tone, and context.
      $sourceDesc.
      $targetDesc.
      
      Respond STRICTLY with a valid JSON object matching this schema:
      {
        "translatedText": "the natural translation",
        "detectedLanguage": "en" | "pt" | "es" | "other",
        "explanation": "brief contextual note or breakdown of idioms used (in Portuguese)",
        "confidenceScore": 0.98
      }
      Do not include markdown triple backticks around the JSON.
    """.trimIndent()

    val request = GenerateContentRequest(
      systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
      contents = listOf(
        Content(
          parts = listOf(Part(text = "Translate this text:\n\n$text"))
        )
      ),
      generationConfig = GenerationConfig(
        temperature = 0.2f
      )
    )

    try {
      val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
      val rawText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
        ?: return@withContext Result.failure(Exception("Nenhuma resposta gerada pela IA."))

      val cleanedJson = cleanJsonString(rawText)
      val json = JSONObject(cleanedJson)

      val translated = json.optString("translatedText", "").ifEmpty { rawText }
      val detectedCode = json.optString("detectedLanguage", "en")
      val explanation = json.optString("explanation", "")
      val confidence = json.optDouble("confidenceScore", 0.98).toFloat()

      val detectedLang = Language.fromCode(detectedCode)
      val effectiveSource = if (sourceLanguage == Language.AUTO) detectedLang else sourceLanguage

      Result.success(
        TranslationResult(
          originalText = text,
          translatedText = translated,
          sourceLang = effectiveSource,
          targetLang = targetLanguage,
          detectedLang = detectedLang,
          confidenceScore = confidence,
          contextualExplanation = explanation,
          isAiPowered = true,
          engineName = "Gemini 3.5 Flash"
        )
      )
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Gera uma análise pedagógica aprofundada para o Modo Estudo usando IA.
   */
  suspend fun generateStudyBreakdownWithAi(
    originalText: String,
    translatedText: String
  ): Result<StudyBreakdown> = withContext(Dispatchers.IO) {
    val apiKey = getApiKey()
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext Result.failure(IllegalStateException("Chave da API Gemini não configurada."))
    }

    val systemPrompt = """
      You are a language teacher helping a student understand this sentence in depth.
      Break down the sentence into individual words/phrases, grammar structure, verb tense, and actionable study tips.
      
      Respond STRICTLY with a valid JSON object matching this schema:
      {
        "grammarStructure": "e.g., Sujeito + Present Perfect Continuous + Objeto + Expressão de Duração",
        "tenseIdentified": "e.g., Present Perfect Continuous (Ação iniciada no passado que continua até o presente)",
        "studyTip": "Dica prática para memorização e uso diário",
        "alternativeTranslations": ["Tradução alternativa 1", "Tradução alternativa 2"],
        "words": [
          {
            "word": "word or short phrase",
            "translation": "tradução em português",
            "partOfSpeech": "Substantivo / Verbo / Preposição / etc.",
            "grammarNote": "Nota gramatical ou contexto",
            "phonetic": "pronúncia aproximada",
            "exampleEn": "Frase de exemplo em inglês",
            "examplePt": "Tradução do exemplo"
          }
        ]
      }
      Do not include markdown triple backticks around the JSON.
    """.trimIndent()

    val request = GenerateContentRequest(
      systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
      contents = listOf(
        Content(
          parts = listOf(
            Part(text = "Sentence: $originalText\nTranslation: $translatedText")
          )
        )
      ),
      generationConfig = GenerationConfig(temperature = 0.3f)
    )

    try {
      val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
      val rawText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
        ?: return@withContext Result.failure(Exception("Nenhuma resposta gerada pela IA."))

      val cleanedJson = cleanJsonString(rawText)
      val json = JSONObject(cleanedJson)

      val grammarStructure = json.optString("grammarStructure", "")
      val tenseIdentified = json.optString("tenseIdentified", "")
      val studyTip = json.optString("studyTip", "")

      val altArray = json.optJSONArray("alternativeTranslations")
      val alternatives = mutableListOf<String>()
      if (altArray != null) {
        for (i in 0 until altArray.length()) {
          alternatives.add(altArray.getString(i))
        }
      }

      val wordsArray = json.optJSONArray("words") ?: JSONArray()
      val wordsList = mutableListOf<WordBreakdown>()
      for (i in 0 until wordsArray.length()) {
        val wObj = wordsArray.getJSONObject(i)
        wordsList.add(
          WordBreakdown(
            word = wObj.optString("word", ""),
            translation = wObj.optString("translation", ""),
            partOfSpeech = wObj.optString("partOfSpeech", ""),
            grammarNote = wObj.optString("grammarNote", ""),
            phonetic = wObj.optString("phonetic", ""),
            exampleEn = wObj.optString("exampleEn", ""),
            examplePt = wObj.optString("examplePt", "")
          )
        )
      }

      Result.success(
        StudyBreakdown(
          fullSentence = originalText,
          fullTranslation = translatedText,
          words = wordsList,
          grammarStructure = grammarStructure,
          tenseIdentified = tenseIdentified,
          studyTip = studyTip,
          alternativeTranslations = alternatives
        )
      )
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  /**
   * Converte Bitmap para Base64 para envio multimodal se necessário.
   */
  fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
  }

  private fun cleanJsonString(raw: String): String {
    var cleaned = raw.trim()
    if (cleaned.startsWith("```json")) {
      cleaned = cleaned.removePrefix("```json")
    } else if (cleaned.startsWith("```")) {
      cleaned = cleaned.removePrefix("```")
    }
    if (cleaned.endsWith("```")) {
      cleaned = cleaned.removeSuffix("```")
    }
    return cleaned.trim()
  }
}
