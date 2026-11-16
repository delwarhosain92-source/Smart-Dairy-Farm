package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.TransactionEntity
import com.example.data.preferences.UserRole
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.HorizontalProportionBar
import com.example.ui.components.IncomeExpenseBarChart
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
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.DairyUiState
import com.example.util.DateUtils

private val INCOME_CATEGORIES = listOf("MILK_SALE", "CATTLE_SALE", "MANURE_SALE", "OTHER")
private val EXPENSE_CATEGORIES = listOf("FEED_PURCHASE", "VET_MEDICINE", "LABOR", "UTILITIES", "EQUIPMENT", "OTHER")

@Composable
fun FinanceReportsScreen(
  selectedTab: Int,
  onSelectTab: (Int) -> Unit,
  uiState: DairyUiState,
  onSaveTransaction: (TransactionEntity) -> Unit,
  onDeleteTransaction: (Long) -> Unit,
  onSwitchToAdmin: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val isAdmin = uiState.settings.userRole == UserRole.ADMIN
  val currentTab = selectedTab.coerceIn(0, 1)
  var showAddTxSheet by remember { mutableStateOf(false) }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize(),
    ) {
      TabRow(
        selectedTabIndex = currentTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = EmeraldPrimary,
      ) {
        Tab(
          selected = currentTab == 0,
          onClick = { onSelectTab(0) },
          text = { Text(strings.tabFinance) },
          icon = { Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp)) },
          modifier = Modifier.testTag("tab_finance"),
        )
        Tab(
          selected = currentTab == 1,
          onClick = { onSelectTab(1) },
          text = { Text(strings.tabReports) },
          icon = { Icon(Icons.Rounded.Assessment, contentDescription = null, modifier = Modifier.size(18.dp)) },
          modifier = Modifier.testTag("tab_reports"),
        )
      }

      if (currentTab == 0) {
        FinanceLedgerTab(
          uiState = uiState,
          isAdmin = isAdmin,
          onAddTransactionClick = { showAddTxSheet = true },
          onDeleteTransaction = onDeleteTransaction,
          onSwitchToAdmin = onSwitchToAdmin,
        )
      } else {
        ReportsAnalyticsTab(uiState = uiState)
      }
    }

    if (currentTab == 0 && isAdmin) {
      FloatingActionButton(
        onClick = { showAddTxSheet = true },
        containerColor = EmeraldPrimary,
        contentColor = PureWhite,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(18.dp)
          .testTag("fab_add_transaction"),
      ) {
        Icon(Icons.Rounded.Add, contentDescription = strings.addTransaction)
      }
    }
  }

  if (showAddTxSheet) {
    TransactionFormSheet(
      onSave = {
        onSaveTransaction(it)
        showAddTxSheet = false
      },
      onDismiss = { showAddTxSheet = false },
    )
  }
}

@Composable
private fun FinanceLedgerTab(
  uiState: DairyUiState,
  isAdmin: Boolean,
  onAddTransactionClick: () -> Unit,
  onDeleteTransaction: (Long) -> Unit,
  onSwitchToAdmin: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val currency = LocalCurrencySymbol.current
  var filterType by remember { mutableStateOf("ALL") }

  val filteredTx = remember(uiState.transactions, filterType) {
    uiState.transactions.filter { filterType == "ALL" || it.type == filterType }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 92.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    if (!isAdmin) {
      item {
        Card(
          shape = MaterialTheme.shapes.medium,
          colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.7f)),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(Icons.Rounded.Lock, contentDescription = null, tint = WarmAmber, modifier = Modifier.size(18.dp))
              Text(strings.farmerFinanceNoticeTitle, style = MaterialTheme.typography.titleSmall, color = WarmAmber)
            }
            Text(strings.farmerFinanceNoticeBody, style = MaterialTheme.typography.bodySmall)
            OutlinedButton(onClick = onSwitchToAdmin) {
              Text(strings.switchToAdminBtn)
            }
          }
        }
      }
    }

    // Balance Summary Card
    item {
      Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = EmeraldDark),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Text(
            text = strings.netBalance,
            style = MaterialTheme.typography.labelLarge,
            color = PureWhite.copy(alpha = 0.8f),
          )
          Text(
            text = DateUtils.formatCurrency(uiState.netBalance, currency),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color = PureWhite,
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column {
              Text(strings.totalIncome, style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.75f))
              Text(
                text = "+${DateUtils.formatCurrency(uiState.totalIncome, currency)}",
                style = MaterialTheme.typography.titleMedium,
                color = FreshLeaf,
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(strings.totalExpense, style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.75f))
              Text(
                text = "-${DateUtils.formatCurrency(uiState.totalExpense, currency)}",
                style = MaterialTheme.typography.titleMedium,
                color = CoralContainer,
              )
            }
          }
        }
      }
    }

    // Filter Chips
    item {
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = filterType == "ALL", onClick = { filterType = "ALL" }, label = { Text(strings.filterAll) })
        FilterChip(selected = filterType == "INCOME", onClick = { filterType = "INCOME" }, label = { Text(strings.incomeLabel) })
        FilterChip(selected = filterType == "EXPENSE", onClick = { filterType = "EXPENSE" }, label = { Text(strings.expenseLabel) })
      }
    }

    if (filteredTx.isEmpty()) {
      item {
        EmptyStateCard(
          icon = Icons.Rounded.AccountBalanceWallet,
          title = strings.emptyFinanceTitle,
          subtitle = strings.emptyFinanceSubtitle,
          actionLabel = if (isAdmin) strings.addTransaction else null,
          onAction = if (isAdmin) onAddTransactionClick else null,
        )
      }
    } else {
      items(filteredTx, key = { it.id }) { tx ->
        val isIncome = tx.type == "INCOME"
        Surface(
          shape = MaterialTheme.shapes.medium,
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Surface(
              shape = CircleShape,
              color = if (isIncome) MintSoft else CoralContainer,
              modifier = Modifier.size(40.dp),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (isIncome) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward,
                  contentDescription = null,
                  tint = if (isIncome) EmeraldPrimary else AlertCoral,
                  modifier = Modifier.size(20.dp),
                )
              }
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = strings.transactionCategoryDisplay(tx.category),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
              )
              if (tx.description.isNotBlank()) {
                Text(
                  text = tx.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Text(
                text = DateUtils.formatFullDate(tx.dateEpochDay, strings),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }

            Text(
              text = "${if (isIncome) "+" else "-"}${DateUtils.formatCurrency(tx.amount, currency)}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = if (isIncome) EmeraldPrimary else AlertCoral,
            )

            if (isAdmin) {
              IconButton(
                onClick = { onDeleteTransaction(tx.id) },
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = strings.delete, tint = AlertCoral, modifier = Modifier.size(20.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ReportsAnalyticsTab(uiState: DairyUiState) {
  val strings = LocalAppStrings.current
  val currency = LocalCurrencySymbol.current
  val context = LocalContext.current

  val total7DaysMilk = uiState.weeklyMilkSeries.sumOf { it.totalLiters }
  val avgDailyMilk = if (uiState.weeklyMilkSeries.isNotEmpty()) total7DaysMilk / uiState.weeklyMilkSeries.size else 0.0

  val expenseByCategory = remember(uiState.transactions) {
    uiState.transactions
      .filter { it.type == "EXPENSE" }
      .groupBy { it.category }
      .mapValues { (_, list) -> list.sumOf { it.amount } }
      .entries
      .sortedByDescending { it.value }
  }
  val maxExpenseCat = expenseByCategory.firstOrNull()?.value ?: 1.0

  val breedCounts = remember(uiState.cattle) {
    uiState.cattle.groupBy { it.breed }.mapValues { it.value.size }.entries.sortedByDescending { it.value }
  }
  val totalHerd = uiState.cattle.size.coerceAtLeast(1)

  val healthCounts = remember(uiState.cattle) {
    uiState.cattle.groupBy { it.healthStatus }.mapValues { it.value.size }.entries.sortedByDescending { it.value }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    // Header + Share Offline Summary
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = strings.farmReportsTitle,
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.weight(1f),
        )
        OutlinedButton(
          onClick = {
            val reportText = buildString {
              appendLine("${strings.reportSummaryHeader} (${uiState.settings.farmName})")
              appendLine("------------------------------")
              appendLine("${strings.totalCattle}: ${uiState.cattle.size} (${strings.milkingCows}: ${uiState.milkingCowsCount})")
              appendLine("${strings.todaysMilk}: ${DateUtils.formatOneDecimal(uiState.todayMilkTotal)} ${strings.litersUnit}")
              appendLine("${strings.totalMilk7Days}: ${DateUtils.formatOneDecimal(total7DaysMilk)} ${strings.litersUnit}")
              appendLine("${strings.avgDailyMilk}: ${DateUtils.formatOneDecimal(avgDailyMilk)} ${strings.litersUnit}")
              appendLine("${strings.totalIncome}: ${DateUtils.formatCurrency(uiState.totalIncome, currency)}")
              appendLine("${strings.totalExpense}: ${DateUtils.formatCurrency(uiState.totalExpense, currency)}")
              appendLine("${strings.netBalance}: ${DateUtils.formatCurrency(uiState.netBalance, currency)}")
            }
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_SUBJECT, strings.reportSummaryHeader)
              putExtra(Intent.EXTRA_TEXT, reportText)
            }
            context.startActivity(Intent.createChooser(sendIntent, strings.shareReport))
          },
          modifier = Modifier.testTag("share_report_button"),
        ) {
          Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(strings.shareReport)
        }
      }
    }

    // 7-Day Milk Output Summary + Chart
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
          Text(strings.milkLast7Days, style = MaterialTheme.typography.titleMedium)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Surface(shape = MaterialTheme.shapes.small, color = MintSoft, modifier = Modifier.weight(1f)) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(strings.totalMilk7Days, style = MaterialTheme.typography.labelSmall, color = EmeraldDark)
                Text(
                  text = "${DateUtils.formatOneDecimal(total7DaysMilk)} ${strings.litersUnit}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = EmeraldDark,
                )
              }
            }
            Surface(shape = MaterialTheme.shapes.small, color = MintSoft, modifier = Modifier.weight(1f)) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(strings.avgDailyMilk, style = MaterialTheme.typography.labelSmall, color = EmeraldDark)
                Text(
                  text = "${DateUtils.formatOneDecimal(avgDailyMilk)} ${strings.litersUnit}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = EmeraldDark,
                )
              }
            }
          }
          WeeklyMilkBarChart(series = uiState.weeklyMilkSeries)
        }
      }
    }

    // Income vs Expense Comparison
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
          Text(strings.incomeVsExpense, style = MaterialTheme.typography.titleMedium)
          IncomeExpenseBarChart(
            income = uiState.totalIncome,
            expense = uiState.totalExpense,
            currencySymbol = currency,
          )
        }
      }
    }

    // Expense Breakdown
    if (expenseByCategory.isNotEmpty()) {
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
            Text(strings.expenseBreakdown, style = MaterialTheme.typography.titleMedium)
            expenseByCategory.forEach { (cat, amt) ->
              HorizontalProportionBar(
                label = strings.transactionCategoryDisplay(cat),
                valueText = DateUtils.formatCurrency(amt, currency),
                fraction = (amt / maxExpenseCat).toFloat(),
                barColor = WarmAmber,
              )
            }
          }
        }
      }
    }

    // Breed & Health Distribution
    if (uiState.cattle.isNotEmpty()) {
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
            Text(strings.breedBreakdown, style = MaterialTheme.typography.titleMedium)
            breedCounts.forEach { (breed, count) ->
              HorizontalProportionBar(
                label = strings.breedDisplay(breed),
                valueText = count.toString(),
                fraction = count.toFloat() / totalHerd.toFloat(),
                barColor = EmeraldPrimary,
              )
            }
          }
        }
      }

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
            Text(strings.herdComposition, style = MaterialTheme.typography.titleMedium)
            healthCounts.forEach { (status, count) ->
              val color = when (status) {
                "HEALTHY" -> FreshLeaf
                "UNDER_TREATMENT" -> AlertCoral
                "PREGNANT" -> MilkSky
                else -> WarmAmber
              }
              HorizontalProportionBar(
                label = strings.healthStatusLabel(status),
                valueText = count.toString(),
                fraction = count.toFloat() / totalHerd.toFloat(),
                barColor = color,
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionFormSheet(
  onSave: (TransactionEntity) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var type by remember { mutableStateOf("INCOME") }
  var category by remember { mutableStateOf(INCOME_CATEGORIES.first()) }
  var amountText by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(strings.addTransaction, style = MaterialTheme.typography.headlineSmall)

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChip(
          selected = type == "INCOME",
          onClick = {
            type = "INCOME"
            category = INCOME_CATEGORIES.first()
          },
          label = { Text(strings.incomeLabel) },
        )
        FilterChip(
          selected = type == "EXPENSE",
          onClick = {
            type = "EXPENSE"
            category = EXPENSE_CATEGORIES.first()
          },
          label = { Text(strings.expenseLabel) },
        )
      }

      Text(strings.categoryLabel, style = MaterialTheme.typography.labelLarge)
      val cats = if (type == "INCOME") INCOME_CATEGORIES else EXPENSE_CATEGORIES
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        cats.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(strings.transactionCategoryDisplay(cat)) },
          )
        }
      }

      OutlinedTextField(
        value = amountText,
        onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
        label = { Text(strings.amountLabel) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("tx_amount_input"),
      )

      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text(strings.descriptionLabel) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )

      if (errorText != null) {
        Text(errorText ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss) { Text(strings.cancel) }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            val amt = amountText.toDoubleOrNull()
            if (amt == null || amt <= 0.0) {
              errorText = strings.validationPositiveNumber
            } else {
              onSave(
                TransactionEntity(
                  type = type,
                  category = category,
                  amount = amt,
                  dateEpochDay = DateUtils.todayEpochDay(),
                  description = description.trim(),
                )
              )
            }
          },
          modifier = Modifier.testTag("save_tx_button"),
        ) { Text(strings.save) }
      }
    }
  }
}
