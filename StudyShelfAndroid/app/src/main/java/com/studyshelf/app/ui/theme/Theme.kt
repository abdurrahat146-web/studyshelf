package com.studyshelf.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StudyShelfColorScheme = darkColorScheme(
    primary = StudyShelfGold,
    secondary = StudyShelfOrange,
    background = StudyShelfBg,
    surface = StudyShelfSurface,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    onBackground = StudyShelfText,
    onSurface = StudyShelfText,
    error = StudyShelfRed
)

@Composable
fun StudyShelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudyShelfColorScheme,
        typography = StudyShelfTypography,
        content = content
    )
}
