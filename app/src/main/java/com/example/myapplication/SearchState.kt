package com.example.myapplication

sealed interface SearchState {

    fun getTextLength(): Int

    data object Empty : SearchState {
        override fun getTextLength() = 0
    }

    data class Value(val value: String) : SearchState {
        override fun getTextLength() = value.length
    }

    data class CardNumber(val cardNumber: String) : SearchState {
        override fun getTextLength() = cardNumber.length
    }
}
