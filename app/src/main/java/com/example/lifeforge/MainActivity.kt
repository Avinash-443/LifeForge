package com.example.lifeforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.example.lifeforge.ui.LifeForgeApp
import com.example.lifeforge.ui.LifeForgeViewModelFactory
import com.example.lifeforge.ui.LocalLifeForgeViewModelFactory
import com.example.lifeforge.ui.theme.LifeForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModelFactory = LifeForgeViewModelFactory()
        setContent {
            LifeForgeTheme {
                CompositionLocalProvider(LocalLifeForgeViewModelFactory provides viewModelFactory) {
                    LifeForgeApp()
                }
            }
        }
    }
}
