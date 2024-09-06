package com.example.myapplication

class SuggestionsCache(private val maxSize: Int) {
    private val list = mutableSetOf<SuggestionCacheItem>()

    fun add(element: SuggestionCacheItem) {
        if (list.size == maxSize) {
            list.remove(list.first())
        }
        list.add(element)
    }

    fun get(value: String) = list.lastOrNull { it.value == value }

}