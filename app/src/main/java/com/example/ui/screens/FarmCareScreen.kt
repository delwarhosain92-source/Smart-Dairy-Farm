package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Grass
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.CattleEntity
import com.example.data.local.FeedItemEntity
import com.example.data.local.FeedLogEntity
import com.example.data.local.HealthRecordEntity
import com.example.data.preferences.UserRole
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FreshLeaf
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.LocalCurrencySymbol
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmAmber
import com.example.util.DateUtils
import kotlin.math.roundToInt

private val FEED_CATEGORIES = listOf(
  "GREEN_FODDER",
  "DRY_ROUGHAGE",
  "CONCENTRATE",
  "SILAGE",
  "MINERAL_SUPPLEMENT",
)

private val HEALTH_RECORD_TYPES = listOf("VACCINATION", "TREATMENT", "DEWORMING", "CHECKUP")

@Composable
fun FarmCareScreen(
  selectedTab: Int,
  onSelectTab: (Int) -> Unit,
  feedItems: List<FeedItemEntity>,
  feedLogs: List<FeedLogEntity>,
  healthRecords: List<HealthRecordEntity>,
  cattleList: List<CattleEntity>,
  userRole: UserRole,
  onSaveFeedItem: (FeedItemEntity) -> Unit,
  onDeleteFeedItem: (Long) -> Unit,
  onLogFeedConsumption: (FeedItemEntity, Double, String) -> Unit,
  onRestockFeed: (FeedItemEntity, Double, String, Boolean) -> Unit,
  onOpenAddHealth: () -> Unit,
  onToggleHealthCompleted: (HealthRecordEntity) -> Unit,
  onDeleteHealthRecord: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val isAdmin = userRole == UserRole.ADMIN
  val currentTab = selectedTab.coerceIn(0, 1)

  var showFeedItemSheet by remember { mutableStateOf(false) }
  var editingFeedItem by remember { mutableStateOf<FeedItemEntity?>(null) }
  var feedActionTarget by remember { mutableStateOf<Pair<FeedItemEntity, Boolean>?>(null) } // Boolean = isRestock

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
          text = { Text(strings.tabFeed) },
          icon = { Icon(Icons.Rounded.Grass, contentDescription = null, modifier = Modifier.size(18.dp)) },
          modifier = Modifier.testTag("tab_feed"),
        )
        Tab(
          selected = currentTab == 1,
          onClick = { onSelectTab(1) },
          text = { Text(strings.tabHealth) },
          icon = { Icon(Icons.Rounded.Vaccines, contentDescription = null, modifier = Modifier.size(18.dp)) },
          modifier = Modifier.testTag("tab_health"),
        )
      }

      if (currentTab == 0) {
        FeedInventoryTab(
          feedItems = feedItems,
          feedLogs = feedLogs,
          isAdmin = isAdmin,
          onAddFeed = {
            editingFeedItem = null
            showFeedItemSheet = true
          },
          onLogFeeding = { item -> feedActionTarget = item to false },
          onRestock = { item -> feedActionTarget = item to true },
          onDeleteFeed = onDeleteFeedItem,
        )
      } else {
        HealthRecordsTab(
          healthRecords = healthRecords,
          cattleList = cattleList,
          isAdmin = isAdmin,
          onAddHealth = onOpenAddHealth,
          onToggleCompleted = onToggleHealthCompleted,
          onDeleteHealth = onDeleteHealthRecord,
        )
      }
    }

    if (currentTab == 0 && isAdmin) {
      FloatingActionButton(
        onClick = {
          editingFeedItem = null
          showFeedItemSheet = true
        },
        containerColor = EmeraldPrimary,
        contentColor = PureWhite,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(18.dp)
          .testTag("fab_add_feed"),
      ) {
        Icon(Icons.Rounded.Add, contentDescription = strings.addFeedItem)
      }
    } else if (currentTab == 1 && cattleList.isNotEmpty()) {
      FloatingActionButton(
        onClick = onOpenAddHealth,
        containerColor = WarmAmber,
        contentColor = PureWhite,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(18.dp)
          .testTag("fab_add_health"),
      ) {
        Icon(Icons.Rounded.Add, contentDescription = strings.addHealthRecord)
      }
    }
  }

  if (showFeedItemSheet) {
    FeedItemFormSheet(
      existing = editingFeedItem,
      onSave = {
        onSaveFeedItem(it)
        showFeedItemSheet = false
      },
      onDismiss = { showFeedItemSheet = false },
    )
  }

  feedActionTarget?.let { (item, isRestock) ->
    FeedQuantityActionSheet(
      item = item,
      isRestock = isRestock,
      onConfirm = { qty, notes, alsoExpense ->
        if (isRestock) {
          onRestockFeed(item, qty, notes, alsoExpense)
        } else {
          onLogFeedConsumption(item, qty, notes)
        }
        feedActionTarget = null
      },
      onDismiss = { feedActionTarget = null },
    )
  }
}

@Composable
private fun FeedInventoryTab(
  feedItems: List<FeedItemEntity>,
  feedLogs: List<FeedLogEntity>,
  isAdmin: Boolean,
  onAddFeed: () -> Unit,
  onLogFeeding: (FeedItemEntity) -> Unit,
  onRestock: (FeedItemEntity) -> Unit,
  onDeleteFeed: (Long) -> Unit,
) {
  val strings = LocalAppStrings.current
  val currency = LocalCurrencySymbol.current

  if (feedItems.isEmpty()) {
    Box(modifier = Modifier.padding(16.dp)) {
      EmptyStateCard(
        icon = Icons.Rounded.Inventory,
        title = strings.emptyFeedTitle,
        subtitle = strings.emptyFeedSubtitle,
        actionLabel = if (isAdmin) strings.addFeedItem else null,
        onAction = if (isAdmin) onAddFeed else null,
      )
    }
    return
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 92.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    items(feedItems, key = { it.id }) { item ->
      val isLow = item.stockKg <= item.reorderThresholdKg
      val daysLeft = if (item.dailyUsageKg > 0.0) (item.stockKg / item.dailyUsageKg).roundToInt() else 0
      val fillRatio = (item.stockKg / (item.reorderThresholdKg * 3.0).coerceAtLeast(100.0))
        .toFloat()
        .coerceIn(0.05f, 1f)

      Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
          1.dp,
          if (isLow) WarmAmber.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                text = "${strings.feedCategoryDisplay(item.category)} • ${DateUtils.formatCurrency(item.costPerKg, currency)}/${strings.kgUnit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Surface(
              shape = RoundedCornerShape(50),
              color = if (isLow) AmberContainer else MintSoft,
            ) {
              Text(
                text = if (isLow) strings.lowStockBadge else strings.inStockBadge,
                style = MaterialTheme.typography.labelSmall,
                color = if (isLow) WarmAmber else EmeraldDark,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              )
            }
          }

          // Stock Progress Bar
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(50))
              .background(MaterialTheme.colorScheme.surfaceVariant),
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(fillRatio)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(if (isLow) WarmAmber else FreshLeaf),
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = "${DateUtils.formatOneDecimal(item.stockKg)} ${strings.kgUnit}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "~$daysLeft ${strings.daysShort} ${strings.daysLeftLabel}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            OutlinedButton(
              onClick = { onLogFeeding(item) },
              modifier = Modifier.weight(1f),
            ) {
              Text(strings.logFeeding, maxLines = 1)
            }
            if (isAdmin) {
              Button(
                onClick = { onRestock(item) },
                modifier = Modifier.weight(1f),
              ) {
                Icon(Icons.Rounded.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.restockFeed, maxLines = 1)
              }
              IconButton(
                onClick = { onDeleteFeed(item.id) },
                modifier = Modifier.minimumInteractiveComponentSize(),
              ) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = strings.delete, tint = AlertCoral)
              }
            }
          }
        }
      }
    }

    if (feedLogs.isNotEmpty()) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = strings.recentFeedLogs,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
      }
      items(feedLogs.take(8), key = { "feed_log_${it.id}" }) { log ->
        val isRestock = log.actionType == "RESTOCKED"
        Surface(
          shape = MaterialTheme.shapes.small,
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(log.feedName, style = MaterialTheme.typography.titleSmall)
              Text(
                text = DateUtils.formatFullDate(log.dateEpochDay, strings),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Text(
              text = "${if (isRestock) "+" else "-"}${DateUtils.formatOneDecimal(log.quantityKg)} ${strings.kgUnit}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = if (isRestock) EmeraldPrimary else WarmAmber,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun HealthRecordsTab(
  healthRecords: List<HealthRecordEntity>,
  cattleList: List<CattleEntity>,
  isAdmin: Boolean,
  onAddHealth: () -> Unit,
  onToggleCompleted: (HealthRecordEntity) -> Unit,
  onDeleteHealth: (Long) -> Unit,
) {
  val strings = LocalAppStrings.current
  val currency = LocalCurrencySymbol.current
  val today = DateUtils.todayEpochDay()
  var filter by remember { mutableStateOf("ALL") }

  val filtered = remember(healthRecords, filter) {
    healthRecords.filter { rec ->
      when (filter) {
        "DUE" -> !rec.isCompleted
        "VACCINATION" -> rec.recordType == "VACCINATION"
        "MEDICAL" -> rec.recordType == "TREATMENT" || rec.recordType == "DEWORMING" || rec.recordType == "CHECKUP"
        else -> true
      }
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      FilterChip(selected = filter == "ALL", onClick = { filter = "ALL" }, label = { Text(strings.filterAll) })
      FilterChip(selected = filter == "DUE", onClick = { filter = "DUE" }, label = { Text(strings.filterDue) })
      FilterChip(selected = filter == "VACCINATION", onClick = { filter = "VACCINATION" }, label = { Text(strings.filterVaccines) })
      FilterChip(selected = filter == "MEDICAL", onClick = { filter = "MEDICAL" }, label = { Text(strings.filterMedical) })
    }

    if (filtered.isEmpty()) {
      Box(modifier = Modifier.padding(16.dp)) {
        EmptyStateCard(
          icon = Icons.Rounded.Vaccines,
          title = strings.emptyHealthTitle,
          subtitle = strings.emptyHealthSubtitle,
          actionLabel = if (cattleList.isNotEmpty()) strings.addHealthRecord else null,
          onAction = if (cattleList.isNotEmpty()) onAddHealth else null,
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 92.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        items(filtered, key = { it.id }) { rec ->
          val isOverdue = !rec.isCompleted && rec.nextDueEpochDay != null && rec.nextDueEpochDay < today
          Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = rec.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                  Text(
                    text = "${rec.cowName} (${rec.cowTagId}) • ${strings.healthRecordTypeDisplay(rec.recordType)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }

                val badgeBg = when {
                  rec.isCompleted -> MintSoft
                  isOverdue -> CoralContainer
                  else -> AmberContainer
                }
                val badgeFg = when {
                  rec.isCompleted -> EmeraldDark
                  isOverdue -> AlertCoral
                  else -> WarmAmber
                }
                val badgeText = when {
                  rec.isCompleted -> strings.completedBadge
                  isOverdue -> strings.overdueBadge
                  else -> strings.dueBadge
                }
                Surface(shape = RoundedCornerShape(50), color = badgeBg) {
                  Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = badgeFg,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  )
                }
              }

              val detailsLine = buildString {
                append(DateUtils.formatFullDate(rec.dateEpochDay, strings))
                if (rec.vetName.isNotBlank()) append(" • ${rec.vetName}")
                if (rec.cost > 0) append(" • ${DateUtils.formatCurrency(rec.cost, currency)}")
              }
              Text(
                text = detailsLine,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )

              if (rec.notes.isNotBlank()) {
                Text(
                  text = rec.notes,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                OutlinedButton(
                  onClick = { onToggleCompleted(rec) },
                  modifier = Modifier.testTag("toggle_health_${rec.id}"),
                ) {
                  Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(if (rec.isCompleted) strings.completedBadge else strings.markCompleted)
                }
                if (isAdmin) {
                  IconButton(
                    onClick = { onDeleteHealth(rec.id) },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  ) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = strings.delete, tint = AlertCoral)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedItemFormSheet(
  existing: FeedItemEntity?,
  onSave: (FeedItemEntity) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var name by remember(existing) { mutableStateOf(existing?.name ?: "") }
  var category by remember(existing) { mutableStateOf(existing?.category ?: FEED_CATEGORIES.first()) }
  var stockKg by remember(existing) { mutableStateOf(existing?.stockKg?.toInt()?.toString() ?: "") }
  var dailyUsageKg by remember(existing) { mutableStateOf(existing?.dailyUsageKg?.toInt()?.toString() ?: "20") }
  var reorderKg by remember(existing) { mutableStateOf(existing?.reorderThresholdKg?.toInt()?.toString() ?: "80") }
  var costPerKg by remember(existing) { mutableStateOf(existing?.costPerKg?.toString() ?: "10") }
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
      Text(
        text = if (existing == null) strings.addFeedItem else strings.editFeedItem,
        style = MaterialTheme.typography.headlineSmall,
      )
      OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text(strings.feedNameLabel) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )
      Text(strings.feedCategoryLabel, style = MaterialTheme.typography.labelLarge)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        FEED_CATEGORIES.forEach { cat ->
          FilterChip(
            selected = category == cat,
            onClick = { category = cat },
            label = { Text(strings.feedCategoryDisplay(cat)) },
          )
        }
      }
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = stockKg,
          onValueChange = { stockKg = it.filter { c -> c.isDigit() || c == '.' } },
          label = { Text(strings.stockKgLabel) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
          value = dailyUsageKg,
          onValueChange = { dailyUsageKg = it.filter { c -> c.isDigit() || c == '.' } },
          label = { Text(strings.dailyUsageKgLabel) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.weight(1f),
        )
      }
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = reorderKg,
          onValueChange = { reorderKg = it.filter { c -> c.isDigit() || c == '.' } },
          label = { Text(strings.reorderLevelKgLabel) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
          value = costPerKg,
          onValueChange = { costPerKg = it.filter { c -> c.isDigit() || c == '.' } },
          label = { Text(strings.costPerKgLabel) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.weight(1f),
        )
      }
      if (errorText != null) {
        Text(errorText ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss) { Text(strings.cancel) }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            val s = stockKg.toDoubleOrNull()
            val d = dailyUsageKg.toDoubleOrNull() ?: 10.0
            val r = reorderKg.toDoubleOrNull() ?: 50.0
            val c = costPerKg.toDoubleOrNull() ?: 0.0
            when {
              name.isBlank() -> errorText = strings.validationRequired
              s == null || s < 0.0 -> errorText = strings.validationPositiveNumber
              else -> onSave(
                FeedItemEntity(
                  id = existing?.id ?: 0L,
                  name = name.trim(),
                  category = category,
                  stockKg = s,
                  dailyUsageKg = d,
                  reorderThresholdKg = r,
                  costPerKg = c,
                )
              )
            }
          },
        ) { Text(strings.save) }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedQuantityActionSheet(
  item: FeedItemEntity,
  isRestock: Boolean,
  onConfirm: (Double, String, Boolean) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var qtyText by remember { mutableStateOf(if (isRestock) "100" else item.dailyUsageKg.toInt().toString()) }
  var notes by remember { mutableStateOf("") }
  var alsoRecordExpense by remember { mutableStateOf(true) }
  var errorText by remember { mutableStateOf<String?>(null) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = "${if (isRestock) strings.restockFeed else strings.logFeeding}: ${item.name}",
        style = MaterialTheme.typography.titleLarge,
      )
      OutlinedTextField(
        value = qtyText,
        onValueChange = { qtyText = it.filter { c -> c.isDigit() || c == '.' } },
        label = { Text(strings.quantityKgLabel) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )
      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text(strings.notesOptional) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )
      if (isRestock && item.costPerKg > 0.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Checkbox(checked = alsoRecordExpense, onCheckedChange = { alsoRecordExpense = it })
          Text(strings.autoRecordExpense, style = MaterialTheme.typography.bodyMedium)
        }
      }
      if (errorText != null) {
        Text(errorText ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss) { Text(strings.cancel) }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            val qty = qtyText.toDoubleOrNull()
            if (qty == null || qty <= 0.0) {
              errorText = strings.validationPositiveNumber
            } else {
              onConfirm(qty, notes, alsoRecordExpense)
            }
          },
        ) { Text(strings.confirm) }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordModalSheet(
  cattleList: List<CattleEntity>,
  preselectedCow: CattleEntity? = null,
  onSave: (CattleEntity, String, String, Long, Long?, String, Double, String, Boolean) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val today = DateUtils.todayEpochDay()

  var selectedCow by remember(preselectedCow, cattleList) {
    mutableStateOf(preselectedCow ?: cattleList.firstOrNull())
  }
  var recordType by remember { mutableStateOf("VACCINATION") }
  var title by remember { mutableStateOf("") }
  var nextDueDays by remember { mutableStateOf("") }
  var vetName by remember { mutableStateOf("") }
  var costText by remember { mutableStateOf("0") }
  var notes by remember { mutableStateOf("") }
  var isCompleted by remember { mutableStateOf(false) }
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
      Text(strings.addHealthRecord, style = MaterialTheme.typography.headlineSmall)

      Text(strings.selectCow, style = MaterialTheme.typography.labelLarge)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        cattleList.forEach { cow ->
          FilterChip(
            selected = selectedCow?.id == cow.id,
            onClick = { selectedCow = cow },
            label = { Text("${cow.name} (${cow.cowTagId})") },
          )
        }
      }

      Text(strings.recordTypeLabel, style = MaterialTheme.typography.labelLarge)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        HEALTH_RECORD_TYPES.forEach { type ->
          FilterChip(
            selected = recordType == type,
            onClick = { recordType = type },
            label = { Text(strings.healthRecordTypeDisplay(type)) },
          )
        }
      }

      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text(strings.recordTitleLabel) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("health_title_input"),
      )

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = nextDueDays,
          onValueChange = { nextDueDays = it.filter { c -> c.isDigit() } },
          label = { Text(strings.nextDueDaysLabel) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.weight(1f),
        )
        OutlinedTextField(
          value = costText,
          onValueChange = { costText = it.filter { c -> c.isDigit() || c == '.' } },
          label = { Text(strings.costLabel) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.weight(1f),
        )
      }

      OutlinedTextField(
        value = vetName,
        onValueChange = { vetName = it },
        label = { Text(strings.vetNameLabel) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )

      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text(strings.notesOptional) },
        modifier = Modifier.fillMaxWidth(),
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = isCompleted, onCheckedChange = { isCompleted = it })
        Text(strings.completedBadge, style = MaterialTheme.typography.bodyMedium)
      }

      if (errorText != null) {
        Text(errorText ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
      }

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss) { Text(strings.cancel) }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            val cow = selectedCow
            val dueDays = nextDueDays.toLongOrNull()
            val cost = costText.toDoubleOrNull() ?: 0.0
            when {
              cow == null || title.isBlank() -> errorText = strings.validationRequired
              else -> onSave(
                cow,
                recordType,
                title,
                today,
                dueDays?.let { today + it },
                vetName,
                cost,
                notes,
                isCompleted,
              )
            }
          },
          modifier = Modifier.testTag("save_health_button"),
        ) { Text(strings.save) }
      }
    }
  }
}
