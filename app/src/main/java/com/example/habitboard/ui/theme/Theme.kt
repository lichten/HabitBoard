package com.example.habitboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GiraffeColorScheme = lightColorScheme(
    primary                = GiraffeBrownDark,
    onPrimary              = GiraffeCream,
    primaryContainer       = Color(0xFFFFDDB5),
    onPrimaryContainer     = Color(0xFF1F0900),
    secondary              = GiraffeBrownMid,
    onSecondary            = Color.White,
    secondaryContainer     = Color(0xFFFFE0BC),
    onSecondaryContainer   = Color(0xFF2B1400),
    tertiary               = GiraffeAmber,
    onTertiary             = Color.White,
    tertiaryContainer      = Color(0xFFFFDDB0),
    onTertiaryContainer    = Color(0xFF2C1700),
    background             = GiraffeYellow,
    onBackground           = GiraffeBrownDeep,
    surface                = GiraffeYellowLight,
    onSurface              = GiraffeBrownDeep,
    surfaceVariant         = GiraffeGold,
    onSurfaceVariant       = GiraffeBrownOnVar,
    error                  = Color(0xFFBA1A1A),
    onError                = Color.White,
)

@Composable
fun HabitBoardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GiraffeColorScheme,
        typography = Typography,
        content = content
    )
}
