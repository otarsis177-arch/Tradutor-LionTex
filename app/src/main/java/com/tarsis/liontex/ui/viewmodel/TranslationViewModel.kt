package com.tarsis.liontex.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tarsis.liontex.data.ai.GeminiTranslationService
import com.tarsis.liontex.data.local.HistoryRepository
import com.tarsis.liontex.data.local.LionTexDatabaseHelper
import com.tarsis.liontex.data.network.NetworkMonitor
import com.tarsis.liontex.data.ocr.MlKitOcrEngine
import com.tarsis.liontex.data.translation.StudyBreakdownEngine
import com.tarsis.liontex.data.translation.TranslationEngine
import com.tarsis.liontex.data.tts.LionTexTtsManager
import com.tarsis.liontex.domain.model.FlashcardItem
import com.tarsis.liontex.domain.model.HistoryItem
import com.tarsis.liontex.domain.model.Language
import com.tarsis.liontex.domain.model.OcrResult
import com.tarsis.liontex.domain.model.StudyBreakdown
import com.tarsis.liontex.domain.model.TranslationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class TranslationViewModel(application: Application) : AndroidViewModel(application) {

  private val dbHelper = LionTexDatabaseHelper(application)
  val repository = HistoryRepository(dbHelper)
  val ttsManager = LionTexTtsManager(application)
  private val networkMonitor = NetworkMonitor(application)

  val isOnline: StateFlow<Boolean> = networkMonitor.isOnlineFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), networkMonitor.isOnline)

  // Estados de Tradução de Texto
  private val _inputText = MutableStateFlow("I have been studying English for two years.")
  val inputText: StateFlow<String> = _inputText.asStateFlow()

  private val _sourceLanguage = MutableStateFlow(Language.ENGLISH)
  val sourceLanguage: StateFlow<Language> = _sourceLanguage.asStateFlow()

  private val _targetLanguage = MutableStateFlow(Language.PORTUGUESE)
  val targetLanguage: StateFlow<Language> = _targetLanguage.asStateFlow()

  private val _isTranslating = MutableStateFlow(false)
  val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

  private val _translationResult = MutableStateFlow<TranslationResult?>(null)
  val translationResult: StateFlow<TranslationResult?> = _translationResult.asStateFlow()

  private val _studyBreakdown = MutableStateFlow<StudyBreakdown?>(null)
  val studyBreakdown: StateFlow<StudyBreakdown?> = _studyBreakdown.asStateFlow()

  private val _isStudyLoading = MutableStateFlow(false)
  val isStudyLoading: StateFlow<Boolean> = _isStudyLoading.asStateFlow()

  // Preferência por IA ativada
  private val _useAiMode = MutableStateFlow(true)
  val useAiMode: StateFlow<Boolean> = _useAiMode.asStateFlow()

  // Estados de OCR e Imagem
  private val _selectedImageUri = MutableStateFlow<Uri?>(null)
  val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

  private val _selectedImageBitmap = MutableStateFlow<Bitmap?>(null)
  val selectedImageBitmap: StateFlow<Bitmap?> = _selectedImageBitmap.asStateFlow()

  private val _isOcrLoading = MutableStateFlow(false)
  val isOcrLoading: StateFlow<Boolean> = _isOcrLoading.asStateFlow()

  private val _ocrResult = MutableStateFlow<OcrResult?>(null)
  val ocrResult: StateFlow<OcrResult?> = _ocrResult.asStateFlow()

  private val _ocrError = MutableStateFlow<String?>(null)
  val ocrError: StateFlow<String?> = _ocrError.asStateFlow()

  // Estados de Interface e Feedback
  private val _userFeedbackMessage = MutableStateFlow<String?>(null)
  val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

  val historyList: StateFlow<List<HistoryItem>> = repository.historyList
  val flashcardsList: StateFlow<List<FlashcardItem>> = repository.flashcardsList

  init {
    viewModelScope.launch {
      repository.refreshAll()
      // Executa tradução inicial padrão
      performTranslation(saveToHistory = false)
    }
  }

  fun updateInputText(text: String) {
    _inputText.value = text
  }

  fun setSourceLanguage(language: Language) {
    _sourceLanguage.value = language
  }

  fun setTargetLanguage(language: Language) {
    _targetLanguage.value = language
  }

  fun toggleAiMode(enabled: Boolean) {
    _useAiMode.value = enabled
  }

  fun swapLanguages() {
    val currentSource = _sourceLanguage.value
    val currentTarget = _targetLanguage.value

    if (currentSource != Language.AUTO) {
      _sourceLanguage.value = currentTarget
      _targetLanguage.value = currentSource

      // Inverte o texto se houver resultado
      _translationResult.value?.let { currentResult ->
        _inputText.value = currentResult.translatedText
        performTranslation(saveToHistory = false)
      }
    }
  }

  fun performTranslation(saveToHistory: Boolean = true) {
    val text = _inputText.value.trim()
    if (text.isEmpty()) {
      _translationResult.value = null
      _studyBreakdown.value = null
      return
    }

    _isTranslating.value = true

    viewModelScope.launch {
      val online = networkMonitor.isOnline
      val shouldUseAi = _useAiMode.value && online && GeminiTranslationService.isAiAvailable()

      var finalResult: TranslationResult? = null

      if (shouldUseAi) {
        val aiResult = GeminiTranslationService.translateWithAi(
          text = text,
          sourceLanguage = _sourceLanguage.value,
          targetLanguage = _targetLanguage.value
        )

        aiResult.onSuccess { res ->
          finalResult = res
        }.onFailure {
          // Fallback gracioso para o motor local robusto
          finalResult = TranslationEngine.translate(
            text = text,
            sourceLanguage = _sourceLanguage.value,
            targetLanguage = _targetLanguage.value
          )
        }
      } else {
        finalResult = TranslationEngine.translate(
          text = text,
          sourceLanguage = _sourceLanguage.value,
          targetLanguage = _targetLanguage.value
        )
      }

      finalResult?.let { res ->
        _translationResult.value = res
        _isTranslating.value = false

        // Carrega a análise pedagógica (IA se online ou fallback local)
        loadStudyBreakdown(res.originalText, res.translatedText, shouldUseAi)

        if (saveToHistory) {
          repository.saveTranslation(
            original = res.originalText,
            translated = res.translatedText,
            sourceLang = res.sourceLang.code,
            targetLang = res.targetLang.code,
            sourceMode = if (res.isAiPowered) "ai_text" else "text"
          )
        }
      } ?: run {
        _isTranslating.value = false
      }
    }
  }

  private fun loadStudyBreakdown(original: String, translated: String, useAi: Boolean) {
    _isStudyLoading.value = true
    viewModelScope.launch {
      if (useAi) {
        val aiStudy = GeminiTranslationService.generateStudyBreakdownWithAi(original, translated)
        aiStudy.onSuccess { breakdown ->
          _studyBreakdown.value = breakdown
          _isStudyLoading.value = false
          return@launch
        }
      }

      // Fallback local
      val localStudy = StudyBreakdownEngine.analyzeSentence(original, translated)
      _studyBreakdown.value = localStudy
      _isStudyLoading.value = false
    }
  }

  fun onImageSelected(uri: Uri?) {
    _selectedImageUri.value = uri
    _ocrResult.value = null
    _ocrError.value = null

    if (uri == null) {
      _selectedImageBitmap.value = null
      return
    }

    viewModelScope.launch {
      try {
        val context = getApplication<Application>()
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          val source = ImageDecoder.createSource(context.contentResolver, uri)
          ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            decoder.isMutableRequired = true
          }
        } else {
          @Suppress("DEPRECATION")
          MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        _selectedImageBitmap.value = bitmap
        // Inicia automaticamente o processamento OCR
        processOcr(bitmap)
      } catch (e: Exception) {
        _ocrError.value = "Falha ao carregar imagem: ${e.localizedMessage}"
      }
    }
  }

  fun processOcr(bitmap: Bitmap) {
    _isOcrLoading.value = true
    _ocrError.value = null

    viewModelScope.launch {
      val result = MlKitOcrEngine.processImage(bitmap)
      _isOcrLoading.value = false

      result.onSuccess { ocr ->
        _ocrResult.value = ocr
        if (ocr.fullCorrectedText.isNotBlank()) {
          // Preenche a caixa de tradução com o texto da imagem
          _inputText.value = ocr.fullCorrectedText
          _sourceLanguage.value = ocr.detectedLanguage
          _targetLanguage.value = Language.PORTUGUESE
          performTranslation(saveToHistory = true)
        }
      }.onFailure { err ->
        _ocrError.value = "Erro no reconhecimento de texto: ${err.localizedMessage}"
      }
    }
  }

  fun speakText(text: String, isEnglish: Boolean = true) {
    val locale = if (isEnglish) Locale.US else Locale("pt", "BR")
    ttsManager.speak(text, locale)
  }

  fun stopSpeaking() {
    ttsManager.stop()
  }

  fun toggleFavorite(item: HistoryItem) {
    viewModelScope.launch {
      repository.toggleFavorite(item.id, item.isFavorite)
    }
  }

  fun deleteHistoryItem(id: Long) {
    viewModelScope.launch {
      repository.deleteHistoryItem(id)
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      repository.clearAllHistory()
    }
  }

  fun saveAsFlashcard(front: String, back: String, hint: String = "") {
    viewModelScope.launch {
      repository.addFlashcard(front, back, hint)
      _userFeedbackMessage.value = "Adicionado aos Flashcards de Estudo!"
    }
  }

  fun reviewFlashcard(id: Long, mastered: Boolean) {
    viewModelScope.launch {
      repository.recordFlashcardReview(id, mastered)
    }
  }

  fun clearFeedbackMessage() {
    _userFeedbackMessage.value = null
  }

  override fun onCleared() {
    super.onCleared()
    ttsManager.shutdown()
  }
}
