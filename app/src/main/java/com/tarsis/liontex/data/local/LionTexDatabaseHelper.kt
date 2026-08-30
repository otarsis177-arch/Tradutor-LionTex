package com.tarsis.liontex.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tarsis.liontex.domain.model.FlashcardItem
import com.tarsis.liontex.domain.model.HistoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class LionTexDatabaseHelper(context: Context) : SQLiteOpenHelper(
  context,
  DATABASE_NAME,
  null,
  DATABASE_VERSION
) {

  companion object {
    private const val DATABASE_NAME = "liontex_translator.db"
    private const val DATABASE_VERSION = 1

    private const val TABLE_HISTORY = "history"
    private const val TABLE_FLASHCARDS = "flashcards"

    // Colunas History
    private const val COL_ID = "id"
    private const val COL_ORIGINAL = "original_text"
    private const val COL_TRANSLATED = "translated_text"
    private const val COL_SOURCE_LANG = "source_lang"
    private const val COL_TARGET_LANG = "target_lang"
    private const val COL_TIMESTAMP = "timestamp"
    private const val COL_IS_FAVORITE = "is_favorite"
    private const val COL_SOURCE_MODE = "source_mode"

    // Colunas Flashcards
    private const val COL_FC_ID = "id"
    private const val COL_FC_FRONT = "front_text"
    private const val COL_FC_BACK = "back_text"
    private const val COL_FC_HINT = "hint"
    private const val COL_FC_REVIEW_COUNT = "review_count"
    private const val COL_FC_LAST_REVIEWED = "last_reviewed"
    private const val COL_FC_IS_MASTERED = "is_mastered"
  }

  override fun onCreate(db: SQLiteDatabase) {
    val createHistoryTable = """
      CREATE TABLE $TABLE_HISTORY (
        $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_ORIGINAL TEXT NOT NULL,
        $COL_TRANSLATED TEXT NOT NULL,
        $COL_SOURCE_LANG TEXT NOT NULL,
        $COL_TARGET_LANG TEXT NOT NULL,
        $COL_TIMESTAMP INTEGER NOT NULL,
        $COL_IS_FAVORITE INTEGER DEFAULT 0,
        $COL_SOURCE_MODE TEXT DEFAULT 'text'
      )
    """.trimIndent()

    val createFlashcardsTable = """
      CREATE TABLE $TABLE_FLASHCARDS (
        $COL_FC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COL_FC_FRONT TEXT NOT NULL,
        $COL_FC_BACK TEXT NOT NULL,
        $COL_FC_HINT TEXT DEFAULT '',
        $COL_FC_REVIEW_COUNT INTEGER DEFAULT 0,
        $COL_FC_LAST_REVIEWED INTEGER DEFAULT 0,
        $COL_FC_IS_MASTERED INTEGER DEFAULT 0
      )
    """.trimIndent()

    db.execSQL(createHistoryTable)
    db.execSQL(createFlashcardsTable)

    // Insere dados iniciais de estudo para o usuário praticar imediatamente
    insertInitialCards(db)
  }

  private fun insertInitialCards(db: SQLiteDatabase) {
    val initialCards = listOf(
      Triple("I have been studying English for two years.", "Eu estudo inglês há dois anos.", "Present Perfect Continuous"),
      Triple("I need to improve my English.", "Eu preciso melhorar meu inglês.", "Expressão de Necessidade"),
      Triple("Piece of cake!", "Algo muito fácil! (Moleza)", "Expressão Idiomática"),
      Triple("Break a leg!", "Boa sorte!", "Expressão Idiomática"),
      Triple("Hit the books.", "Estudar com afinco.", "Gíria / Expressão de Estudo")
    )

    for (card in initialCards) {
      val values = ContentValues().apply {
        put(COL_FC_FRONT, card.first)
        put(COL_FC_BACK, card.second)
        put(COL_FC_HINT, card.third)
        put(COL_FC_REVIEW_COUNT, 0)
        put(COL_FC_LAST_REVIEWED, System.currentTimeMillis())
        put(COL_FC_IS_MASTERED, 0)
      }
      db.insert(TABLE_FLASHCARDS, null, values)
    }
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
    db.execSQL("DROP TABLE IF EXISTS $TABLE_FLASHCARDS")
    onCreate(db)
  }
}

class HistoryRepository(private val dbHelper: LionTexDatabaseHelper) {

  private val _historyList = MutableStateFlow<List<HistoryItem>>(emptyList())
  val historyList: StateFlow<List<HistoryItem>> = _historyList.asStateFlow()

  private val _flashcardsList = MutableStateFlow<List<FlashcardItem>>(emptyList())
  val flashcardsList: StateFlow<List<FlashcardItem>> = _flashcardsList.asStateFlow()

  suspend fun refreshAll() = withContext(Dispatchers.IO) {
    loadHistory()
    loadFlashcards()
  }

  private fun loadHistory() {
    val list = mutableListOf<HistoryItem>()
    val db = dbHelper.readableDatabase
    val cursor = db.rawQuery("SELECT * FROM history ORDER BY timestamp DESC", null)
    cursor.use {
      while (it.moveToNext()) {
        val item = HistoryItem(
          id = it.getLong(it.getColumnIndexOrThrow("id")),
          originalText = it.getString(it.getColumnIndexOrThrow("original_text")),
          translatedText = it.getString(it.getColumnIndexOrThrow("translated_text")),
          sourceLangCode = it.getString(it.getColumnIndexOrThrow("source_lang")),
          targetLangCode = it.getString(it.getColumnIndexOrThrow("target_lang")),
          timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp")),
          isFavorite = it.getInt(it.getColumnIndexOrThrow("is_favorite")) == 1,
          sourceMode = it.getString(it.getColumnIndexOrThrow("source_mode"))
        )
        list.add(item)
      }
    }
    _historyList.value = list
  }

  private fun loadFlashcards() {
    val list = mutableListOf<FlashcardItem>()
    val db = dbHelper.readableDatabase
    val cursor = db.rawQuery("SELECT * FROM flashcards ORDER BY id DESC", null)
    cursor.use {
      while (it.moveToNext()) {
        val item = FlashcardItem(
          id = it.getLong(it.getColumnIndexOrThrow("id")),
          frontText = it.getString(it.getColumnIndexOrThrow("front_text")),
          backText = it.getString(it.getColumnIndexOrThrow("back_text")),
          hint = it.getString(it.getColumnIndexOrThrow("hint")),
          reviewCount = it.getInt(it.getColumnIndexOrThrow("review_count")),
          lastReviewed = it.getLong(it.getColumnIndexOrThrow("last_reviewed")),
          isMastered = it.getInt(it.getColumnIndexOrThrow("is_mastered")) == 1
        )
        list.add(item)
      }
    }
    _flashcardsList.value = list
  }

  suspend fun saveTranslation(
    original: String,
    translated: String,
    sourceLang: String,
    targetLang: String,
    sourceMode: String = "text"
  ): Long = withContext(Dispatchers.IO) {
    if (original.isBlank() || translated.isBlank()) return@withContext -1L

    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
      put("original_text", original.trim())
      put("translated_text", translated.trim())
      put("source_lang", sourceLang)
      put("target_lang", targetLang)
      put("timestamp", System.currentTimeMillis())
      put("is_favorite", 0)
      put("source_mode", sourceMode)
    }
    val id = db.insert("history", null, values)
    loadHistory()
    id
  }

  suspend fun toggleFavorite(id: Long, currentStatus: Boolean) = withContext(Dispatchers.IO) {
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
      put("is_favorite", if (currentStatus) 0 else 1)
    }
    db.update("history", values, "id = ?", arrayOf(id.toString()))
    loadHistory()
  }

  suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
    val db = dbHelper.writableDatabase
    db.delete("history", "id = ?", arrayOf(id.toString()))
    loadHistory()
  }

  suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
    val db = dbHelper.writableDatabase
    db.delete("history", null, null)
    loadHistory()
  }

  suspend fun addFlashcard(front: String, back: String, hint: String = "") = withContext(Dispatchers.IO) {
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
      put("front_text", front.trim())
      put("back_text", back.trim())
      put("hint", hint.trim())
      put("review_count", 0)
      put("last_reviewed", System.currentTimeMillis())
      put("is_mastered", 0)
    }
    db.insert("flashcards", null, values)
    loadFlashcards()
  }

  suspend fun recordFlashcardReview(id: Long, mastered: Boolean) = withContext(Dispatchers.IO) {
    val db = dbHelper.writableDatabase
    val cursor = db.rawQuery("SELECT review_count FROM flashcards WHERE id = ?", arrayOf(id.toString()))
    var count = 0
    cursor.use {
      if (it.moveToFirst()) {
        count = it.getInt(0)
      }
    }

    val values = ContentValues().apply {
      put("review_count", count + 1)
      put("last_reviewed", System.currentTimeMillis())
      put("is_mastered", if (mastered) 1 else 0)
    }
    db.update("flashcards", values, "id = ?", arrayOf(id.toString()))
    loadFlashcards()
  }
}
