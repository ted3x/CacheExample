package com.example.myapplication

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginEnd
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val viewModel = ViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        setContentView(R.layout.activity_main)

        findViewById<EditText>(R.id.editText).apply {
            doOnTextChanged { text, start, before, count ->
                viewModel.handleSearch(text.toString())
            }
        }
        lifecycleScope.launch {
            val text = findViewById<TextView>(R.id.textView)
            viewModel.suggestions.collectLatest { state ->
                text.text = state.joinToString { it.value }
            }
        }
    }
}

