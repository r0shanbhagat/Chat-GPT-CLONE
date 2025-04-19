package com.codentmind.gemlens.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color.White,
    primaryContainer = Color(0xFF1D1C1C),
    secondaryContainer = Color(0xFF353434),
    inversePrimary = Color(0xFFD8D3D3),
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.Black,

//    primary = ThemeColors.Dark.primary,
//    onPrimary = ThemeColors.Dark.text,
//    surface = ThemeColors.Dark.surface,
//    background = ThemeColors.Dark.background
)

private val LightColorScheme = lightColorScheme(
    primary = Color.Black,
    primaryContainer = Color.White,
    secondaryContainer = Color.LightGray,
    inversePrimary = Color(0xFF777373),
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,

    /* Other default colors to override
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

//    primary = ThemeColors.Light.primary,
//    onPrimary = ThemeColors.Light.text,
//    surface = ThemeColors.Light.surface,
//    background = ThemeColors.Light.background
)

@Composable
fun GemiTheme(
    darkTheme: Boolean = false,//isSystemInDarkTheme(), Not supporting as of now
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*  dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
              val context = LocalContext.current
              if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
          }*/
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as Activity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.window.navigationBarColor = colorScheme.primary.copy(alpha = 0.08f)
                    .compositeOver(colorScheme.surface.copy()).toArgb()
                activity.window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(
                    activity.window,
                    view
                ).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(
                    activity.window,
                    view
                ).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}