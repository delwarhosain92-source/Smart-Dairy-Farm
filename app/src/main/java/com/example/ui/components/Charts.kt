package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FreshLeaf
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.viewmodel.DailyMilkSummary
import com.example.util.DateUtils

@Composable
fun WeeklyMilkBarChart(
  series: List<DailyMilkSummary>,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  var selectedIndex by remember(series) { mutableIntStateOf((series.size - 1).coerceAtLeast(0)) }
  var animateIn by remember { mutableStateOf(false) }
  LaunchedEffect(series) { animateIn = true }
  val progress by animateFloatAsState(
    targetValue = if (animateIn) 1f else 0f,
    animationSpec = tween(durationMillis = 550),
    label = "milkChartAnim",
  )

  val maxVal = (series.maxOfOrNull { it.totalLiters } ?: 10.0).coerceAtLeast(10.0)
  val selected = series.getOrNull(selectedIndex)

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    // Legend + Selected Day Readout
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        LegendDot(color = FreshLeaf, label = strings.morningShift)
        LegendDot(color = EmeraldDark, label = strings.eveningShift)
      }
      if (selected != null) {
        Surface(
          shape = RoundedCornerShape(50),
          color = MintSoft,
        ) {
          Text(
            text = "${selected.dayLabel}: ${DateUtils.formatOneDecimal(selected.totalLiters)} ${strings.litersUnit}",
            style = MaterialTheme.typography.labelMedium,
            color = EmeraldDark,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
      }
    }

    // Bars Canvas
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(145.dp),
    ) {
      val barCount = series.size.coerceAtLeast(1)
      val slotWidth = size.width / barCount
      val barWidth = (slotWidth * 0.44f).coerceAtMost(34.dp.toPx())

      // Subtle horizontal grid lines
      for (i in 0..2) {
        val y = size.height * (i / 2f)
        drawLine(
          color = gridColor,
          start = Offset(0f, y),
          end = Offset(size.width, y),
          strokeWidth = 1.dp.toPx(),
        )
      }

      series.forEachIndexed { index, item ->
        val centerX = slotWidth * index + slotWidth / 2f
        val left = centerX - barWidth / 2f

        val totalHeight = ((item.totalLiters / maxVal).toFloat() * size.height * progress)
          .coerceAtLeast(if (item.totalLiters > 0) 6.dp.toPx() else 0f)
        val eveningHeight = if (item.totalLiters > 0) {
          totalHeight * (item.eveningLiters / item.totalLiters).toFloat()
        } else 0f
        val morningHeight = (totalHeight - eveningHeight).coerceAtLeast(0f)

        val isSelected = index == selectedIndex
        val corner = CornerRadius(6.dp.toPx(), 6.dp.toPx())

        if (totalHeight > 0f) {
          // Evening (bottom segment)
          drawRoundRect(
            color = if (isSelected) EmeraldDark else EmeraldDark.copy(alpha = 0.72f),
            topLeft = Offset(left, size.height - eveningHeight),
            size = Size(barWidth, eveningHeight),
            cornerRadius = corner,
          )
          // Morning (top segment)
          if (morningHeight > 0f) {
            drawRoundRect(
              color = if (isSelected) FreshLeaf else FreshLeaf.copy(alpha = 0.78f),
              topLeft = Offset(left, size.height - totalHeight),
              size = Size(barWidth, morningHeight + 2.dp.toPx()),
              cornerRadius = corner,
            )
          }
        }
      }
    }

    // Tappable Day Labels Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
      series.forEachIndexed { index, item ->
        val isSelected = index == selectedIndex
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MintSoft else Color.Transparent)
            .clickable { selectedIndex = index }
            .padding(vertical = 4.dp),
        ) {
          Text(
            text = item.dayLabel,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            ),
            color = if (isSelected) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Composable
private fun LegendDot(color: Color, label: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(5.dp),
  ) {
    Box(
      modifier = Modifier
        .size(9.dp)
        .clip(CircleShape)
        .background(color),
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun IncomeExpenseBarChart(
  income: Double,
  expense: Double,
  currencySymbol: String,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val maxVal = maxOf(income, expense, 1.0)

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    HorizontalProportionBar(
      label = strings.totalIncome,
      valueText = DateUtils.formatCurrency(income, currencySymbol),
      fraction = (income / maxVal).toFloat(),
      barColor = EmeraldPrimary,
    )
    HorizontalProportionBar(
      label = strings.totalExpense,
      valueText = DateUtils.formatCurrency(expense, currencySymbol),
      fraction = (expense / maxVal).toFloat(),
      barColor = AlertCoral,
    )
  }
}

@Composable
fun HorizontalProportionBar(
  label: String,
  valueText: String,
  fraction: Float,
  barColor: Color,
  modifier: Modifier = Modifier,
) {
  var animateIn by remember { mutableStateOf(false) }
  LaunchedEffect(fraction) { animateIn = true }
  val animatedFraction by animateFloatAsState(
    targetValue = if (animateIn) fraction.coerceIn(0f, 1f) else 0f,
    animationSpec = tween(500),
    label = "barFraction",
  )

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(5.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = valueText,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)
        .clip(RoundedCornerShape(50))
        .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(animatedFraction.coerceAtLeast(if (fraction > 0f) 0.03f else 0f))
          .height(10.dp)
          .clip(RoundedCornerShape(50))
          .background(barColor),
      )
    }
  }
}
