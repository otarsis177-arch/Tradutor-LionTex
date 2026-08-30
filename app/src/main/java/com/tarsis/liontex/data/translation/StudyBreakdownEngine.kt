package com.tarsis.liontex.data.translation

import com.tarsis.liontex.domain.model.StudyBreakdown
import com.tarsis.liontex.domain.model.WordBreakdown

object StudyBreakdownEngine {

  private val grammarDefinitions = mapOf(
    "i" to WordBreakdown("I", "Eu", "Pronome pessoal (sujeito)", "Primeira pessoa do singular. Sempre escrito com letra maiúscula.", "/aɪ/", "I live in Brazil.", "Eu moro no Brasil."),
    "you" to WordBreakdown("You", "Você / Vocês", "Pronome pessoal", "Serve tanto para o singular quanto para o plural.", "/juː/", "You are doing great.", "Você está indo muito bem."),
    "have" to WordBreakdown("have", "ter / possuir (ou verbo auxiliar)", "Verbo", "Auxiliar indispensável em tempos 'Perfect'.", "/hæv/", "I have an idea.", "Eu tenho uma ideia."),
    "been" to WordBreakdown("been", "estado / sido", "Particípio passado de 'to be'", "Usado em tempos perfeitos contínuos.", "/bɪn/", "It has been wonderful.", "Tem sido maravilhoso."),
    "studying" to WordBreakdown("studying", "estudando", "Gerúndio do verbo 'to study'", "Ação contínua ou hábito.", "/ˈstʌd.i.ɪŋ/", "She is studying now.", "Ela está estudando agora."),
    "study" to WordBreakdown("study", "estudo / estudar", "Verbo / Substantivo", "Dedicação ao aprendizado.", "/ˈstʌd.i/", "I study every morning.", "Eu estudo todas as manhãs."),
    "learn" to WordBreakdown("learn", "aprender", "Verbo", "Adquirir novo conhecimento ou habilidade.", "/lɜːrn/", "We learn by doing.", "Aprendemos fazendo."),
    "learning" to WordBreakdown("learning", "aprendendo / aprendizado", "Gerúndio / Substantivo", "Processo de aprendizado.", "/ˈlɜː.nɪŋ/", "Learning is constant.", "O aprendizado é constante."),
    "english" to WordBreakdown("English", "Inglês", "Substantivo próprio / Adjetivo", "Refere-se ao idioma ou à cultura da Inglaterra.", "/ˈɪŋ.ɡlɪʃ/", "English is universal.", "O inglês é universal."),
    "for" to WordBreakdown("for", "por / durante / há (tempo)", "Preposição", "Indica a duração de um período de tempo.", "/fɔːr/", "I stayed for an hour.", "Eu fiquei por uma hora."),
    "two" to WordBreakdown("two", "dois / duas", "Numeral cardinal", "Quantidade de itens ou anos.", "/tuː/", "Two tickets, please.", "Duas passagens, por favor."),
    "years" to WordBreakdown("years", "anos", "Substantivo plural", "Plural regular de 'year' com acréscimo de 's'.", "/jɪərz/", "Five years ago.", "Cinco anos atrás."),
    "need" to WordBreakdown("need", "precisar / necessidade", "Verbo / Substantivo", "Expressa necessidade fundamental.", "/niːd/", "I need water.", "Eu preciso de água."),
    "to" to WordBreakdown("to", "para (partícula de infinitivo)", "Preposição / Marcador", "Antecede verbos no infinitivo (to learn, to speak).", "/tuː/", "I want to go.", "Eu quero ir."),
    "improve" to WordBreakdown("improve", "melhorar / aperfeiçoar", "Verbo", "Tornar algo melhor com prática.", "/ɪmˈpruːv/", "Improve your skills.", "Melhore suas habilidades.")
  )

  fun analyzeSentence(sentence: String, translation: String): StudyBreakdown {
    val cleanSentence = sentence.trim()
    val words = cleanSentence.split(Regex("[\\s,.;!?]+")).filter { it.isNotBlank() }

    val wordBreakdowns = mutableListOf<WordBreakdown>()

    for (word in words) {
      val lower = word.lowercase()
      val breakdown = grammarDefinitions[lower] ?: WordBreakdown(
        word = word,
        translation = TranslationEngine.translate(word).translatedText,
        partOfSpeech = identifyPartOfSpeech(word),
        grammarNote = "Termo contextual na frase.",
        phonetic = "",
        exampleEn = "Practice using '$word' in conversation.",
        examplePt = "Pratique o uso de '$word' em conversas."
      )
      wordBreakdowns.add(breakdown)
    }

    val lowerClean = cleanSentence.lowercase()
    val (tense, structure, tip) = when {
      lowerClean.contains("have been") || lowerClean.contains("has been") -> {
        Triple(
          "Present Perfect Continuous",
          "Sujeito + have/has + been + Verbo(-ing) + Marcador Temporal (for/since)",
          "💡 Dica Liontex: Usamos o Present Perfect Continuous para ações que começaram no passado e continuam até o presente momento. Em português, traduzimos com o presente ('Eu estudo há...') ou com 'Venho estudando'."
        )
      }
      lowerClean.contains("need to") || lowerClean.contains("needs to") -> {
        Triple(
          "Expressão de Necessidade (Modal-like Verb)",
          "Sujeito + need to + Verbo no Infinitivo",
          "💡 Dica Liontex: 'Need to' é sempre seguido de verbo na sua forma base (infinitivo sem alteração de terminação)."
        )
      }
      lowerClean.contains("will") -> {
        Triple(
          "Simple Future (Futuro Simples)",
          "Sujeito + will + Verbo no Infinitivo",
          "💡 Dica Liontex: 'Will' indica uma decisão espontânea ou previsão futura."
        )
      }
      else -> {
        Triple(
          "Estrutura Afirmativa Regular",
          "Sujeito + Verbo Conjugado + Complemento",
          "💡 Dica Liontex: Observe a ordem direta das frases em inglês (SVO: Sujeito + Verbo + Objeto)."
        )
      }
    }

    return StudyBreakdown(
      fullSentence = cleanSentence,
      fullTranslation = translation,
      words = wordBreakdowns,
      grammarStructure = structure,
      tenseIdentified = tense,
      studyTip = tip,
      alternativeTranslations = listOf(
        translation,
        "Adaptação: ${translation.replace("Eu ", "").replaceFirstChar { it.uppercase() }}"
      )
    )
  }

  private fun identifyPartOfSpeech(word: String): String {
    val lower = word.lowercase()
    return when {
      lower.endsWith("ing") -> "Verbo no gerúndio (-ing)"
      lower.endsWith("ed") -> "Verbo no passado (-ed)"
      lower.endsWith("ly") -> "Advérbio de modo (-ly)"
      lower.endsWith("tion") || lower.endsWith("ment") -> "Substantivo"
      else -> "Vocábulo"
    }
  }
}
