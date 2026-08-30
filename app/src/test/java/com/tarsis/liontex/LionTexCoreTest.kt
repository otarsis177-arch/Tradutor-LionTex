package com.tarsis.liontex

import com.tarsis.liontex.data.ocr.OcrCorrectionEngine
import com.tarsis.liontex.data.translation.StudyBreakdownEngine
import com.tarsis.liontex.data.translation.TranslationEngine
import com.tarsis.liontex.domain.model.Language
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LionTexCoreTest {

  @Test
  fun testOcrCorrection_replacesCommonMistakes() {
    val block = OcrCorrectionEngine.processBlock("I need to leam English.")
    assertEquals("I need to learn English.", block.correctedText)
    assertTrue(block.hasWarnings)
  }

  @Test
  fun testLanguageDetection_englishAndPortuguese() {
    val detectedEn = OcrCorrectionEngine.detectLanguage("I have been studying English for two years.")
    assertEquals(Language.ENGLISH, detectedEn)

    val detectedPt = OcrCorrectionEngine.detectLanguage("Eu preciso de ajuda com a tradução em português.")
    assertEquals(Language.PORTUGUESE, detectedPt)
  }

  @Test
  fun testTranslationEngine_idiomaticExpressions() {
    val result = TranslationEngine.translate("Piece of cake")
    assertTrue(result.translatedText.isNotEmpty())
    assertTrue(result.contextualExplanation.contains("Expressão idiomática"))
  }

  @Test
  fun testStudyBreakdownEngine_identifiesGrammarTense() {
    val breakdown = StudyBreakdownEngine.analyzeSentence(
      "I have been studying English for two years.",
      "Eu estudo inglês há dois anos."
    )

    assertEquals("Present Perfect Continuous", breakdown.tenseIdentified)
    assertTrue(breakdown.words.isNotEmpty())
    assertNotNull(breakdown.words.find { it.word.equals("English", ignoreCase = true) })
  }
}
