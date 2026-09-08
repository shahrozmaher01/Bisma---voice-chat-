package com.example

import android.os.Bundle
import android.view.ActionMode
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.BismaMainApp
import com.example.ui.theme.BismaTheme

class MainActivity : ComponentActivity() {
  private var activeActionMode: ActionMode? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Safely wrap window callback to prevent DecorView floating action mode leaks
    val originalCallback = window.callback
    window.callback = object : Window.Callback by originalCallback {
      override fun onWindowStartingActionMode(callback: ActionMode.Callback?): ActionMode? {
        try {
          activeActionMode?.finish()
        } catch (_: Throwable) {}
        val mode = originalCallback.onWindowStartingActionMode(callback)
        activeActionMode = mode
        return mode
      }

      override fun onWindowStartingActionMode(callback: ActionMode.Callback?, type: Int): ActionMode? {
        try {
          activeActionMode?.finish()
        } catch (_: Throwable) {}
        val mode = originalCallback.onWindowStartingActionMode(callback, type)
        activeActionMode = mode
        return mode
      }

      override fun onActionModeStarted(mode: ActionMode?) {
        activeActionMode = mode
        try {
          originalCallback.onActionModeStarted(mode)
        } catch (_: Throwable) {}
      }

      override fun onActionModeFinished(mode: ActionMode?) {
        if (activeActionMode == mode) {
          activeActionMode = null
        }
        try {
          originalCallback.onActionModeFinished(mode)
        } catch (_: Throwable) {}
      }
    }

    setContent {
      BismaTheme {
        BismaMainApp()
      }
    }
  }

  override fun onPause() {
    try {
      activeActionMode?.finish()
    } catch (_: Exception) {}
    activeActionMode = null
    super.onPause()
  }

  override fun onStop() {
    try {
      activeActionMode?.finish()
    } catch (_: Exception) {}
    activeActionMode = null
    super.onStop()
  }

  override fun onDestroy() {
    try {
      activeActionMode?.finish()
    } catch (_: Exception) {}
    activeActionMode = null
    super.onDestroy()
  }
}


