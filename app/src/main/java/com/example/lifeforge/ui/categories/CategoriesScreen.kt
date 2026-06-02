package com.example.lifeforge.ui.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeforge.ui.lifeForgeViewModel
import com.example.lifeforge.ui.theme.LifeForgeColors

@Composable
fun CategoriesScreen(
    modifier: Modifier = Modifier,
    viewModel: CategoriesViewModel = lifeForgeViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LifeForgeColors.background)
    ) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.headlineMedium,
            color = LifeForgeColors.textPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp, bottom = 88.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LifeForgeColors.card)
                        .padding(16.dp)
                ) {
                    val color = parseColor(category.colorHex)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.22f)),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Icon(
                                imageVector = categoryIcon(category.iconName),
                                contentDescription = null,
                                tint = color
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = LifeForgeColors.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (category.description.isNotBlank()) {
                                Text(
                                    text = category.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LifeForgeColors.textSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseColor(hex: String): Color {
    return try {
        Color(hex.toColorInt())
    } catch (_: Exception) {
        LifeForgeColors.primary
    }
}

private fun categoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "school" -> Icons.Default.School
        "work" -> Icons.Default.Work
        "fitness_center" -> Icons.Default.FitnessCenter
        "favorite" -> Icons.Default.Favorite
        "account_balance" -> Icons.Default.AccountBalance
        "menu_book" -> Icons.Default.MenuBook
        "music_note" -> Icons.Default.MusicNote
        "translate" -> Icons.Default.Translate
        "sports_esports" -> Icons.Default.SportsEsports
        "movie" -> Icons.Default.Movie
        "groups" -> Icons.Default.Groups
        else -> Icons.Default.Category
    }
}
