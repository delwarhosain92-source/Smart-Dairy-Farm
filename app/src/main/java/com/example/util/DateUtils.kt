package com.example.util

import com.example.localization.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

object DateUtils {
  private const val MILLIS_PER_DAY = 86_400_000L

  fun todayEpochDay(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 12)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis / MILLIS_PER_DAY
  }

  fun epochDayToMillis(epochDay: Long): Long = epochDay * MILLIS_PER_DAY

  fun epochDayToUtcPickerMillis(epochDay: Long): Long = epochDay * MILLIS_PER_DAY

  fun utcPickerMillisToEpochDay(utcMillis: Long): Long {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
      timeInMillis = utcMillis
      set(Calendar.HOUR_OF_DAY, 12)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis / MILLIS_PER_DAY
  }

  fun dobFromAgeMonths(ageMonths: Int): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, -ageMonths.coerceAtLeast(1))
    cal.set(Calendar.HOUR_OF_DAY, 12)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis / MILLIS_PER_DAY
  }

  fun calculateAgeMonths(dobEpochDay: Long): Int {
    val today = Calendar.getInstance()
    val dob = Calendar.getInstance().apply { timeInMillis = epochDayToMillis(dobEpochDay) }
    val years = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    val months = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH)
    val dayAdjust = if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) -1 else 0
    return (years * 12 + months + dayAdjust).coerceAtLeast(1)
  }

  fun formatDob(epochDay: Long, locale: Locale = Locale.ENGLISH): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", locale)
    return sdf.format(Date(epochDayToMillis(epochDay)))
  }

  fun formatShortDate(epochDay: Long, locale: Locale = Locale.ENGLISH): String {
    val sdf = SimpleDateFormat("dd MMM", locale)
    return sdf.format(Date(epochDayToMillis(epochDay)))
  }

  fun formatFullDate(epochDay: Long, strings: AppStrings, locale: Locale = Locale.ENGLISH): String {
    val today = todayEpochDay()
    return when (epochDay) {
      today -> strings.todayLabel
      today - 1 -> strings.yesterdayLabel
      else -> SimpleDateFormat("dd MMM yyyy", locale).format(Date(epochDayToMillis(epochDay)))
    }
  }

  fun formatDayShortLabel(epochDay: Long): String {
    val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
    return sdf.format(Date(epochDayToMillis(epochDay))).take(3)
  }

  fun formatCurrency(amount: Double, symbol: String): String {
    val rounded = amount.roundToInt()
    val absStr = kotlin.math.abs(rounded).toString()
    val formatted = buildString {
      var count = 0
      for (i in absStr.length - 1 downTo 0) {
        append(absStr[i])
        count++
        if (count % 3 == 0 && i != 0) append(',')
      }
    }.reversed()
    return if (rounded < 0) "-$symbol$formatted" else "$symbol$formatted"
  }

  fun formatOneDecimal(value: Double): String =
    String.format(Locale.US, "%.1f", value)
}
