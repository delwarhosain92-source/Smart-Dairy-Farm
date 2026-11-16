package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.localization.BanglaStrings
import com.example.localization.EnglishStrings

private val LightDairyColorScheme = lightColorScheme(
  primary = EmeraldPrimary,
  onPrimary = PureWhite,
  primaryContainer = MintSoft,
  onPrimaryContainer = EmeraldDark,
  secondary = MeadowGreen,
  onSecondary = PureWhite,
  secondaryContainer = SoftSageSurface,
  onSecondaryContainer = DeepForestInk,
  tertiary = MilkSky,
  onTertiary = PureWhite,
  tertiaryContainer = MilkSkyContainer,
  onTertiaryContainer = DeepForestInk,
  background = PastureBackground,
  onBackground = DeepForestInk,
  surface = PureWhite,
  onSurface = DeepForestInk,
  surfaceVariant = SoftSageSurface,
  onSurfaceVariant = MutedSageText,
  outline = SubtleOutline,
  outlineVariant = MintBorder,
  error = AlertCoral,
  onError = PureWhite,
  errorContainer = CoralContainer,
  onErrorContainer = AlertCoral,
)

private val DarkDairyColorScheme = darkColorScheme(
  primary = DarkPrimary,
  onPrimary = DarkOnPrimary,
  primaryContainer = DarkPrimaryContainer,
  onPrimaryContainer = DarkOnPrimaryContainer,
  secondary = FreshLeaf,
  onSecondary = DarkOnPrimary,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = DarkTextPrimary,
  tertiary = MilkSky,
  onTertiary = PureWhite,
  tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF0E3A53),
  onTertiaryContainer = DarkTextPrimary,
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkSurfaceVariant,
  outlineVariant = DarkPrimaryContainer,
  error = AlertCoral,
  onError = PureWhite,
  errorContainer = CoralContainer,
  onErrorContainer = AlertCoral,
)

private val DairyShapes = Shapes(
  small = RoundedCornerShape(10.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(22.dp),
  extraLarge = RoundedCornerShape(28.dp),
)

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }
val LocalAppStrings = staticCompositionLocalOf<AppStrings> { EnglishStrings }
val LocalCurrencySymbol = staticCompositionLocalOf { "৳" }

@Composable
fun SmartDairyTheme(
  language: AppLanguage = AppLanguage.ENGLISH,
  currencySymbol: String = "৳",
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkDairyColorScheme else LightDairyColorScheme
  val typography = remember(language) { buildAppTypography(language) }
  val strings = remember(language) {
    when (language) {
      AppLanguage.ENGLISH -> EnglishStrings
      AppLanguage.BANGLA -> BanglaStrings
    }
  }

  CompositionLocalProvider(
    LocalAppLanguage provides language,
    LocalAppStrings provides strings,
    LocalCurrencySymbol provides currencySymbol,
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = typography,
      shapes = DairyShapes,
      content = content,
    )
  }
}
