package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NoorDarkColorScheme = darkColorScheme(
    primary = GoldWarm,
    onPrimary = NightDeepBg,
    primaryContainer = EmeraldMedium,
    onPrimaryContainer = PureWhite,
    secondary = GoldMuted,
    onSecondary = NightDeepBg,
    secondaryContainer = NightSurfaceVariant,
    onSecondaryContainer = GoldWarm,
    background = NightDeepBg,
    onBackground = NightTextPrimary,
    surface = NightSurface,
    onSurface = NightTextPrimary,
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = NightTextSecondary,
    outline = BorderGold
)

private val NoorLightColorScheme = lightColorScheme(
    primary = EmeraldDeep,
    onPrimary = PureWhite,
    primaryContainer = EmeraldMedium,
    onPrimaryContainer = PureWhite,
    secondary = GoldMuted,
    onSecondary = PureWhite,
    secondaryContainer = GoldSurface,
    onSecondaryContainer = EmeraldDeep,
    background = IvoryWhite,
    onBackground = MutedTextDark,
    surface = PureWhite,
    onSurface = MutedTextDark,
    surfaceVariant = GoldSurface,
    onSurfaceVariant = SubtitleTextDark,
    outline = BorderGold
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NoorDarkColorScheme else NoorLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NoorTypography,
        content = content
    )
}
