package com.example.lifeforge

import android.app.Application
import com.example.lifeforge.di.AppContainer

class LifeForgeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
