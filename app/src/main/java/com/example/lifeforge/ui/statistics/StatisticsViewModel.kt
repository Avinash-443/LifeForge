package com.example.lifeforge.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeforge.data.repository.AnalyticsRepository
import com.example.lifeforge.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(
    userRepository: UserRepository,
    analyticsRepository: AnalyticsRepository
) : ViewModel() {
    val profile = userRepository.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val analytics = analyticsRepository.observeSnapshot()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
