package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

enum class AppLanguage(val code: String, val label: String, val shortLabel: String) {
  ENGLISH("en", "English", "EN"),
  BANGLA("bn", "বাংলা", "বাংলা");

  companion object {
    fun fromCode(code: String?): AppLanguage =
      entries.firstOrNull { it.code == code } ?: ENGLISH
  }
}

val PlusJakartaSansFontFamily = FontFamily(
  Font(R.font.plus_jakarta_sans, FontWeight.Normal)
)

val NotoSansBengaliFontFamily = FontFamily(
  Font(R.font.noto_sans_bengali, FontWeight.Normal)
)

val JetBrainsMonoFontFamily = FontFamily(
  Font(R.font.jetbrains_mono, FontWeight.Normal)
)

fun buildAppTypography(language: AppLanguage): Typography {
  val primaryFamily = when (language) {
    AppLanguage.ENGLISH -> PlusJakartaSansFontFamily
    AppLanguage.BANGLA -> NotoSansBengaliFontFamily
  }

  return Typography(
    displaySmall = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 32.sp,
      lineHeight = 40.sp,
      letterSpacing = (-0.25).sp,
    ),
    headlineMedium = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 24.sp,
      lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 20.sp,
      lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp,
      lineHeight = 26.sp,
    ),
    titleMedium = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 16.sp,
      lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.sp,
      lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 15.sp,
      lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 13.sp,
      lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 12.sp,
      lineHeight = 18.sp,
    ),
    labelLarge = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.sp,
      lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.SemiBold,
      fontSize = 12.sp,
      lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
      fontFamily = primaryFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 11.sp,
      lineHeight = 16.sp,
    ),
  )
}
