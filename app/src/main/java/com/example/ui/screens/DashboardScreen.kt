package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Grass
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.preferences.UserRole
import com.example.ui.components.KpiMetricCard
import com.example.ui.components.WeeklyMilkBarChart
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FreshLeaf
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalCurrencySymbol
import com.example.ui.theme.MilkSky
import com.example.ui.theme.MilkSkyContainer
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.DairyUiState
import com.example.util.DateUtils

@Composable
fun DashboardScreen(
  uiState: DairyUiState,
  onNavigateCattle: () -> Unit,
  onNavigateMilk: () -> Unit,
  onNavigateFeed: () -> Unit,
  onNavigateVaccines: () -> Unit,
  onNavigateFinance: () -> Unit,
  onNavigateReports: () -> Unit,
  onQuickAddCattle: () -> Unit,
  onQuickLogMilk: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val currency = LocalCurrencySymbol.current
  val isAdmin = uiState.settings.userRole == UserRole.ADMIN
  val today = DateUtils.todayEpochDay()

  val lowStockItems = uiState.feedItems.filter { it.stockKg <= it.reorderThresholdKg }
  val dueVaccines = uiState.healthRecords.filter {
    !it.isCompleted && (it.nextDueEpochDay == null || it.nextDueEpochDay <= today + 7)
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize()
        .testTag("dashboard_list"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Hero Banner Card
      item {
        Card(
          shape = MaterialTheme.shapes.large,
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(176.dp),
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Image(
              painter = painterResource(id = R.drawable.img_hero_dairy_farm),
              contentDescription = uiState.settings.farmName,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize(),
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(
                      EmeraldDark.copy(alpha = 0.90f),
                      EmeraldPrimary.copy(alpha = 0.65f),
                      Color.Black.copy(alpha = 0.25f),
                    )
                  )
                ),
            )
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
              verticalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(
                  shape = RoundedCornerShape(50),
                  color = PureWhite.copy(alpha = 0.20f),
                ) {
                  Text(
                    text = if (isAdmin) strings.welcomeAdmin else strings.welcomeFarmer,
                    style = MaterialTheme.typography.labelMedium,
                    color = PureWhite,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                  )
                }
                Surface(
                  shape = RoundedCornerShape(50),
                  color = FreshLeaf,
                ) {
                  Text(
                    text = "${DateUtils.formatOneDecimal(uiState.todayMilkTotal)} ${strings.litersUnit}",
                    style = MaterialTheme.typography.labelLarge,
                    color = PureWhite,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                  )
                }
              }

              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = uiState.settings.farmName,
                  style = MaterialTheme.typography.headlineMedium,
                  color = PureWhite,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = "${strings.todayOverview} • ${uiState.cattle.size} ${strings.totalCattle}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = PureWhite.copy(alpha = 0.90f),
                )
              }
            }
          }
        }
      }

      // 2x2 KPI Summary Grid
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            KpiMetricCard(
              title = strings.totalCattle,
              value = uiState.cattle.size.toString(),
              subtitle = "${uiState.milkingCowsCount} ${strings.milkingCows}",
              icon = Icons.Rounded.Grass,
              accentColor = EmeraldPrimary,
              containerColor = MintSoft,
              onClick = onNavigateCattle,
              modifier = Modifier
                .weight(1f)
                .testTag("kpi_total_cattle"),
            )
            KpiMetricCard(
              title = strings.todaysMilk,
              value = "${DateUtils.formatOneDecimal(uiState.todayMilkTotal)} ${strings.litersUnit}",
              subtitle = "${DateUtils.formatOneDecimal(uiState.todayMorningMilk)} / ${DateUtils.formatOneDecimal(uiState.todayEveningMilk)}",
              icon = Icons.Rounded.WaterDrop,
              accentColor = MilkSky,
              containerColor = MilkSkyContainer,
              onClick = onNavigateMilk,
              modifier = Modifier
                .weight(1f)
                .testTag("kpi_todays_milk"),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            KpiMetricCard(
              title = strings.vaccineDueAlert,
              value = (dueVaccines.size + uiState.lowStockFeedCount).toString(),
              subtitle = "${lowStockItems.size} ${strings.lowStockBadge}",
              icon = Icons.Rounded.Vaccines,
              accentColor = if (dueVaccines.isNotEmpty() || lowStockItems.isNotEmpty()) WarmAmber else EmeraldPrimary,
              containerColor = if (dueVaccines.isNotEmpty() || lowStockItems.isNotEmpty()) AmberContainer else MintSoft,
              onClick = onNavigateVaccines,
              modifier = Modifier
                .weight(1f)
                .testTag("kpi_alerts"),
            )
            if (isAdmin) {
              KpiMetricCard(
                title = strings.monthlyProfit,
                value = DateUtils.formatCurrency(uiState.netBalance, currency),
                subtitle = "${strings.incomeLabel}: ${DateUtils.formatCurrency(uiState.totalIncome, currency)}",
                icon = Icons.Rounded.AccountBalanceWallet,
                accentColor = if (uiState.netBalance >= 0) EmeraldPrimary else AlertCoral,
                containerColor = if (uiState.netBalance >= 0) MintSoft else CoralContainer,
                onClick = onNavigateFinance,
                modifier = Modifier
                  .weight(1f)
                  .testTag("kpi_net_balance"),
              )
            } else {
              KpiMetricCard(
                title = strings.treatmentAlert,
                value = uiState.underTreatmentCount.toString(),
                subtitle = strings.healthAndVaccination,
                icon = Icons.Rounded.LocalHospital,
                accentColor = if (uiState.underTreatmentCount > 0) AlertCoral else EmeraldPrimary,
                containerColor = if (uiState.underTreatmentCount > 0) CoralContainer else MintSoft,
                onClick = onNavigateVaccines,
                modifier = Modifier
                  .weight(1f)
                  .testTag("kpi_treatment_count"),
              )
            }
          }
        }
      }

      // Quick Actions Grid
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = strings.quickActions,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            QuickActionButton(
              label = strings.actionLogMilk,
              icon = Icons.Rounded.WaterDrop,
              tint = MilkSky,
              bg = MilkSkyContainer,
              onClick = onQuickLogMilk,
              modifier = Modifier
                .weight(1f)
                .testTag("quick_log_milk"),
            )
            if (isAdmin) {
              QuickActionButton(
                label = strings.actionAddCow,
                customPainter = painterResource(id = R.drawable.ic_cow),
                tint = EmeraldPrimary,
                bg = MintSoft,
                onClick = onQuickAddCattle,
                modifier = Modifier
                  .weight(1f)
                  .testTag("quick_add_cow"),
              )
            } else {
              QuickActionButton(
                label = strings.navCattle,
                customPainter = painterResource(id = R.drawable.ic_cow),
                tint = EmeraldPrimary,
                bg = MintSoft,
                onClick = onNavigateCattle,
                modifier = Modifier
                  .weight(1f)
                  .testTag("quick_view_cattle"),
              )
            }
            QuickActionButton(
              label = strings.actionManageFeed,
              icon = Icons.Rounded.Grass,
              tint = EmeraldDark,
              bg = MintSoft,
              onClick = onNavigateFeed,
              modifier = Modifier
                .weight(1f)
                .testTag("quick_feed"),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            QuickActionButton(
              label = strings.actionVaccines,
              icon = Icons.Rounded.Vaccines,
              tint = WarmAmber,
              bg = AmberContainer,
              onClick = onNavigateVaccines,
              modifier = Modifier
                .weight(1f)
                .testTag("quick_vaccines"),
            )
            QuickActionButton(
              label = strings.actionAddFinance,
              icon = Icons.Rounded.AccountBalanceWallet,
              tint = EmeraldPrimary,
              bg = MintSoft,
              onClick = onNavigateFinance,
              modifier = Modifier
                .weight(1f)
                .testTag("quick_finance"),
            )
            QuickActionButton(
              label = strings.actionViewReports,
              icon = Icons.Rounded.Assessment,
              tint = EmeraldDark,
              bg = MintSoft,
              onClick = onNavigateReports,
              modifier = Modifier
                .weight(1f)
                .testTag("quick_reports"),
            )
          }
        }
      }

      // 7-Day Milk Trend Chart Card
      item {
        Card(
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = strings.weeklyMilkTrend,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = strings.actionViewReports,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                  .size(20.dp)
                  .clickable(onClick = onNavigateReports),
              )
            }
            WeeklyMilkBarChart(series = uiState.weeklyMilkSeries)
          }
        }
      }

      // Alerts Section
      item {
        Card(
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.NotificationsActive,
                contentDescription = strings.farmAlerts,
                tint = EmeraldPrimary,
                modifier = Modifier.size(20.dp),
              )
              Text(
                text = strings.farmAlerts,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
            }

            if (lowStockItems.isEmpty() && dueVaccines.isEmpty()) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Icon(
                  imageVector = Icons.Rounded.CheckCircle,
                  contentDescription = null,
                  tint = FreshLeaf,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = strings.noAlertsMessage,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            } else {
              lowStockItems.forEach { feed ->
                AlertRowItem(
                  title = "${strings.lowStockBadge}: ${feed.name}",
                  subtitle = "${DateUtils.formatOneDecimal(feed.stockKg)} ${strings.kgUnit}",
                  isUrgent = feed.stockKg <= feed.reorderThresholdKg * 0.6,
                  onClick = onNavigateFeed,
                )
              }
              dueVaccines.take(3).forEach { record ->
                val isOverdue = record.nextDueEpochDay != null && record.nextDueEpochDay < today
                AlertRowItem(
                  title = "${record.cowName} (${record.cowTagId}) • ${record.title}",
                  subtitle = strings.healthRecordTypeDisplay(record.recordType),
                  isUrgent = isOverdue,
                  onClick = onNavigateVaccines,
                )
              }
            }
          }
        }
      }

      // Recent Activity
      if (uiState.milkRecords.isNotEmpty()) {
        item {
          Text(
            text = strings.recentActivity,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
        }
        items(uiState.milkRecords.take(4), key = { "dash_milk_${it.id}" }) { record ->
          Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = onNavigateMilk),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f),
              ) {
                Surface(
                  shape = CircleShape,
                  color = MilkSkyContainer,
                  modifier = Modifier.size(36.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Rounded.WaterDrop,
                      contentDescription = null,
                      tint = MilkSky,
                      modifier = Modifier.size(18.dp),
                    )
                  }
                }
                Column {
                  Text(
                    text = "${record.cowName} (${record.cowTagId})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                  Text(
                    text = "${DateUtils.formatFullDate(record.dateEpochDay, strings)} • ${
                      if (record.shift == "MORNING") strings.morningShift else strings.eveningShift
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
              Text(
                text = "${DateUtils.formatOneDecimal(record.liters)} ${strings.litersUnit}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = EmeraldPrimary,
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(12.dp)) }
    }
  }
}

@Composable
private fun QuickActionButton(
  label: String,
  tint: Color,
  bg: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  customPainter: Painter? = null,
) {
  Surface(
    shape = MaterialTheme.shapes.medium,
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    tonalElevation = 1.dp,
    modifier = modifier
      .clip(MaterialTheme.shapes.medium)
      .clickable(onClick = onClick),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Surface(
        shape = CircleShape,
        color = bg,
        modifier = Modifier.size(40.dp),
      ) {
        Box(contentAlignment = Alignment.Center) {
          when {
            icon != null -> Icon(
              imageVector = icon,
              contentDescription = label,
              tint = tint,
              modifier = Modifier.size(20.dp),
            )
            customPainter != null -> Icon(
              painter = customPainter,
              contentDescription = label,
              tint = tint,
              modifier = Modifier.size(20.dp),
            )
          }
        }
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun AlertRowItem(
  title: String,
  subtitle: String,
  isUrgent: Boolean,
  onClick: () -> Unit,
) {
  Surface(
    shape = MaterialTheme.shapes.small,
    color = if (isUrgent) CoralContainer else AmberContainer.copy(alpha = 0.65f),
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.small)
      .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Icon(
        imageVector = Icons.Rounded.WarningAmber,
        contentDescription = null,
        tint = if (isUrgent) AlertCoral else WarmAmber,
        modifier = Modifier.size(18.dp),
      )
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(16.dp),
      )
    }
  }
}
