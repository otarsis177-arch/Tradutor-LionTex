package com.tarsis.liontex.data.ocr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint

object ImagePreprocessor {

  /**
   * Redimensiona o bitmap mantendo a proporção para evitar OutOfMemory (OOM) no Galaxy Tab A9
   * enquanto preserva a nitidez ideal para o ML Kit Text Recognition.
   */
  fun scaleForOcr(bitmap: Bitmap, maxDimension: Int = 1600): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxDimension && height <= maxDimension) {
      return bitmap
    }

    val ratio = width.toFloat() / height.toFloat()
    val newWidth: Int
    val newHeight: Int

    if (width > height) {
      newWidth = maxDimension
      newHeight = (maxDimension / ratio).toInt()
    } else {
      newHeight = maxDimension
      newWidth = (maxDimension * ratio).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
  }

  /**
   * Melhora o contraste e converte em escala de cinza otimizada para leitura de caracteres.
   */
  fun enhanceContrast(bitmap: Bitmap, contrast: Float = 1.35f, brightness: Float = -10f): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(outputBitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Matriz de saturação para escala de cinza + matriz de contraste/brilho
    val grayMatrix = ColorMatrix().apply { setSaturation(0f) }

    val contrastMatrix = ColorMatrix(
      floatArrayOf(
        contrast, 0f, 0f, 0f, brightness,
        0f, contrast, 0f, 0f, brightness,
        0f, 0f, contrast, 0f, brightness,
        0f, 0f, 0f, 1f, 0f
      )
    )

    contrastMatrix.preConcat(grayMatrix)
    paint.colorFilter = ColorMatrixColorFilter(contrastMatrix)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    return outputBitmap
  }

  /**
   * Rotaciona o bitmap caso a orientação original da foto esteja inclinada.
   */
  fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    if (degrees == 0f) return bitmap
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
  }
}
