package com.tarsis.liontex.data.ocr

import com.tarsis.liontex.domain.model.OcrBlock

object OcrCorrectionEngine {

  // Dicionário de substituições comuns de OCR em inglês
  private val commonOcrTypos = mapOf(
    "leam" to "learn",
    "leaming" to "learning",
    "leamed" to "learned",
    "modem" to "modern",
    "b/w" to "between",
    "vv" to "w",
    "cl" to "d",
    "ii" to "ll",
    "I0" to "10",
    "teh" to "the",
    "adn" to "and",
    "dont" to "don't",
    "cant" to "can't",
    "wont" to "won't",
    "didnt" to "didn't",
    "isnt" to "isn't",
    "arent" to "aren't",
    "youre" to "you're",
    "theyre" to "they're",
    "weve" to "we've",
    "ive" to "I've",
    "ill" to "I'll"
  )

  /**
   * Processa uma linha ou bloco de texto retornado pelo OCR,
   * identificando potenciais anomalias e aplicando correções seguras.
   */
  fun processBlock(rawText: String, initialConfidence: Float = 0.90f): OcrBlock {
    val words = rawText.split(Regex("\\s+"))
    val correctedWords = mutableListOf<String>()
    var warningsFound = false
    val notes = mutableListOf<String>()

    for (word in words) {
      val cleanWord = word.trim().replace(Regex("[^a-zA-Z0-9']"), "")
      val lower = cleanWord.lowercase()

      if (commonOcrTypos.containsKey(lower)) {
        val replacement = commonOcrTypos[lower]!!
        // Preserva maiúsculas caso a palavra original estivesse em maiúsculas
        val formattedReplacement = when {
          cleanWord.all { it.isUpperCase() } -> replacement.uppercase()
          cleanWord.firstOrNull()?.isUpperCase() == true -> replacement.replaceFirstChar { it.uppercase() }
          else -> replacement
        }
        val correctedWord = word.replace(cleanWord, formattedReplacement)
        correctedWords.add(correctedWord)
        notes.add("'$cleanWord' corrigido para '$formattedReplacement'")
      } else {
        // Verifica se há mistura incomum de dígitos e letras (ex: 'He11o')
        if (cleanWord.matches(Regex(".*[a-zA-Z]+.*")) && cleanWord.matches(Regex(".*[0-9]+.*"))) {
          if (!cleanWord.matches(Regex("^(1st|2nd|3rd|[0-9]+th|[0-9]+k|[0-9]+m|mp3|mp4|4k|3d)$", RegexOption.IGNORE_CASE))) {
            warningsFound = true
            notes.add("Possível caractere incorreto em '$cleanWord'")
          }
        }
        correctedWords.add(word)
      }
    }

    val correctedText = correctedWords.joinToString(" ")
    val noteString = if (notes.isNotEmpty()) notes.joinToString("; ") else null

    return OcrBlock(
      rawText = rawText,
      correctedText = correctedText,
      confidence = if (warningsFound) (initialConfidence * 0.8f) else initialConfidence,
      hasWarnings = warningsFound || notes.isNotEmpty(),
      warningNote = noteString
    )
  }

  /**
   * Detecta se o texto está predominantemente em inglês, português ou espanhol
   * através de análise de marcadores léxicos e caracteres típicos.
   */
  fun detectLanguage(text: String): com.tarsis.liontex.domain.model.Language {
    val lower = text.lowercase()
    if (lower.isBlank()) return com.tarsis.liontex.domain.model.Language.ENGLISH

    // Caracteres fortemente portugueses
    val ptChars = listOf("ã", "õ", "ç", "á", "é", "í", "ó", "ú", "â", "ê", "ô", "à")
    val ptScore = ptChars.count { lower.contains(it) } * 3

    val enWords = listOf(
      "the", "is", "are", "have", "has", "been", "with", "from", "this", "that",
      "what", "where", "when", "why", "how", "you", "they", "we", "for", "about",
      "learn", "study", "english", "screen", "translate", "image", "button"
    )

    val ptWords = listOf(
      "o", "a", "os", "as", "um", "uma", "de", "do", "da", "para", "com", "em",
      "que", "não", "estou", "está", "estão", "tem", "ter", "inglês", "tela"
    )

    val tokens = lower.split(Regex("[^\\p{L}]+")).filter { it.isNotBlank() }
    val enHits = tokens.count { enWords.contains(it) }
    val ptHits = tokens.count { ptWords.contains(it) } + ptScore

    return when {
      ptHits > enHits -> com.tarsis.liontex.domain.model.Language.PORTUGUESE
      else -> com.tarsis.liontex.domain.model.Language.ENGLISH
    }
  }
}
