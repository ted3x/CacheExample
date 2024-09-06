package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class ViewModel {

    var job: Job? = null
    val scope = CoroutineScope(Dispatchers.Main)
    var initialSuggestionsLastTimestamp = -1L
    var initialSuggestions: List<Suggestion>? = null
    val suggestionsCache = SuggestionsCache(10)
    val suggestions = MutableStateFlow<List<Suggestion>>(listOf())
    val mutex = Mutex()

    init {
        handleSearch("")
    }

    fun handleSearch(text: String) {
        job?.cancel()
        job = scope.launch {
            mutex.withLock {
                val state = when {
                    text.isBlank() -> SearchState.Empty
                    text.contains(CARD_REGEX) -> SearchState.CardNumber(text)
                    else -> SearchState.Value(text)
                }
                state.withDelay {
                    handleState(state)
                }
            }
        }
    }

    private suspend fun handleState(state: SearchState) {
        Log.d("ViewModel", "handling state $state")
        when (state) {
            is SearchState.Empty -> updateValue(getInitialSuggestions())
            is SearchState.Value -> {
                suggestionsCache.get(state.value)?.suggestions?.map { it.copy("${it.value}\n From cache") }
                    ?.let {
                        updateValue(it)
                    }
                updateValue(fetchSuggestions(state.value))
            }

            is SearchState.CardNumber -> {
                updateValue(fetchCardNumber(state.cardNumber))
            }
        }

    }

    private suspend fun getInitialSuggestions(): List<Suggestion> {
        if (initialSuggestions != null && initialSuggestionsLastTimestamp > System.currentTimeMillis()) {
            return initialSuggestions!!
        }
        delay(1000)
        Log.d("ViewModel", "getInitialSuggestions")
        // Fetch suggestions from back-end
        initialSuggestionsLastTimestamp = System.currentTimeMillis() + 60_000L
        return listOf(Suggestion(UUID.randomUUID().toString())).also { initialSuggestions = it }
    }

    private suspend fun fetchSuggestions(searchValue: String): List<Suggestion> {
        delay(1000)
        Log.d("ViewModel", "fetchSuggestions")
        // Network Call
        val suggestionsList = listOf(Suggestion(UUID.randomUUID().toString()))
        val item = SuggestionCacheItem(searchValue, suggestionsList)
        suggestionsCache.add(item)
        return suggestionsList.map { it }
    }

    private suspend fun fetchCardNumber(cardNumber: String): List<Suggestion> {
        delay(1000)
        Log.d("ViewModel", "fetchCardNumber")
        return listOf(Suggestion(cardNumber))
    }

    private suspend fun SearchState.withDelay(block: suspend () -> Unit) {
        if (this.getTextLength() > 1) delay(1000)
        block.invoke()
    }

    private fun updateValue(value: List<Suggestion>) {
        Log.d("ViewModel", "Updated value ${value.joinToString { it.value }}")
        suggestions.update { value }
    }
}
