package com.example.myapplication

data class SuggestionCacheItem(
    val value: String,
    val suggestions: List<Suggestion>
)

data class Suggestion(val value: String)