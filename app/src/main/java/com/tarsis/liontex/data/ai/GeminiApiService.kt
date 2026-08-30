package com.tarsis.liontex.data.ai

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
  val contents: List<Content>,
  val generationConfig: GenerationConfig? = null,
  val systemInstruction: Content? = null
)

@Serializable
data class Content(
  val parts: List<Part>,
  val role: String? = null
)

@Serializable
data class Part(
  val text: String? = null,
  val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
  val mimeType: String,
  val data: String
)

@Serializable
data class GenerationConfig(
  val temperature: Float? = 0.2f,
  val topP: Float? = 0.95f,
  val topK: Int? = 40,
  val maxOutputTokens: Int? = 2048
)

@Serializable
data class GenerateContentResponse(
  val candidates: List<Candidate> = emptyList()
)

@Serializable
data class Candidate(
  val content: Content? = null,
  val finishReason: String? = null
)

interface GeminiApiService {
  @POST("v1beta/models/gemini-3.5-flash:generateContent")
  suspend fun generateContent(
    @Query("key") apiKey: String,
    @Body request: GenerateContentRequest
  ): GenerateContentResponse
}

object GeminiRetrofitClient {
  private const val BASE_URL = "https://generativelanguage.googleapis.com/"

  private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BASIC
  }

  private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
  }

  val service: GeminiApiService by lazy {
    val retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
      .build()
    retrofit.create(GeminiApiService::class.java)
  }
}
