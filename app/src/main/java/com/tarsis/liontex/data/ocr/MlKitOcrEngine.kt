package com.tarsis.liontex.data.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tarsis.liontex.domain.model.OcrBlock
import com.tarsis.liontex.domain.model.OcrResult
import kotlinx.coroutines.tasks.await
import kotlin.system.measureTimeMillis

object MlKitOcrEngine {

  private val recognizer by lazy {
    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
  }

  /**
   * Executa o pipeline de OCR na imagem:
   * 1. Escala inteligente (economia de RAM para Galaxy Tab A9)
   * 2. Pré-processamento opcional (contraste/escala de cinza)
   * 3. ML Kit Text Recognition
   * 4. Pós-processamento e validação heurística
   */
  suspend fun processImage(
    bitmap: Bitmap,
    applyEnhancement: Boolean = true
  ): Result<OcrResult> {
    return try {
      var processedResult: OcrResult? = null
      val timeMs = measureTimeMillis {
        val scaledBitmap = ImagePreprocessor.scaleForOcr(bitmap, maxDimension = 1600)
        val finalBitmap = if (applyEnhancement) {
          ImagePreprocessor.enhanceContrast(scaledBitmap)
        } else {
          scaledBitmap
        }

        val inputImage = InputImage.fromBitmap(finalBitmap, 0)
        val visionText = recognizer.process(inputImage).await()

        val blocks = mutableListOf<OcrBlock>()
        val rawSb = StringBuilder()
        val correctedSb = StringBuilder()

        for (textBlock in visionText.textBlocks) {
          for (line in textBlock.lines) {
            val rawLine = line.text
            val block = OcrCorrectionEngine.processBlock(rawLine)
            blocks.add(block)

            rawSb.append(block.rawText).append("\n")
            correctedSb.append(block.correctedText).append("\n")
          }
        }

        val fullRaw = rawSb.toString().trim()
        val fullCorrected = correctedSb.toString().trim()

        val avgConfidence = if (blocks.isNotEmpty()) {
          blocks.map { it.confidence }.average().toFloat()
        } else {
          0f
        }

        val detectedLang = OcrCorrectionEngine.detectLanguage(fullCorrected)

        processedResult = OcrResult(
          fullRawText = fullRaw,
          fullCorrectedText = fullCorrected,
          blocks = blocks,
          confidenceScore = avgConfidence,
          processingTimeMs = 0L,
          detectedLanguage = detectedLang
        )
      }

      val finalWithTime = processedResult!!.copy(processingTimeMs = timeMs)
      Result.success(finalWithTime)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
