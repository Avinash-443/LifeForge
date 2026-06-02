package com.example.lifeforge.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider

val LocalLifeForgeViewModelFactory = compositionLocalOf<ViewModelProvider.Factory> {
    error("LifeForgeViewModelFactory not provided")
}
