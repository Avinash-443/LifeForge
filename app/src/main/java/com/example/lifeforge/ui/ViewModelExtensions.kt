package com.example.lifeforge.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
inline fun <reified T : ViewModel> lifeForgeViewModel(): T =
    viewModel(factory = LocalLifeForgeViewModelFactory.current)
