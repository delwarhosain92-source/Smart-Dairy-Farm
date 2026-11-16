package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.CattleEntity
import com.example.data.local.MilkRecordEntity
import com.example.data.preferences.UserRole
import com.example.ui.components.EmptyStateCard
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EveningIndigo
import com.example.ui.theme.EveningIndigoContainer
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MilkSky
import com.example.ui.theme.MilkSkyContainer
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmAmber
import com.example.util.DateUtils

@Composable
fun MilkScreen(
  milkRecords: List<MilkRecordEntity>,
  cattleList: List<CattleEntity>,
  userRole: UserRole,
  onOpenLogMilk: (Long) -> Unit,
  onDeleteMilkRecord: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val today = DateUtils.todayEpochDay()
  var selectedDate by remember { mutableLongStateOf(today) }
  var shiftFilter by remember { mutableStateOf("ALL") }

  val dayRecords = remember(milkRecords, selectedDate, shiftFilter) {
    milkRecords.filter {
      it.dateEpochDay == selectedDate && (shiftFilter == "ALL" || it.shift == shiftFilter)
    }
  }
  val allDayRecords = remember(milkRecords, selectedDate) {
    milkRecords.filter { it.dateEpochDay == selectedDate }
  }
  val dayTotal = allDayRecords.sumOf { it.liters }
  val morningTotal = allDayRecords.filter { it.shift == "MORNING" }.sumOf { it.liters }
  val eveningTotal = allDayRecords.filter { it.shift == "EVENING" }.sumOf { it.liters }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize(),
    ) {
      // Date Stepper & Daily Yield Summary
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Surface(
          shape = MaterialTheme.shapes.medium,
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            IconButton(
              onClick = { selectedDate -= 1 },
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("milk_prev_day"),
            ) {
              Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "Previous day")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = DateUtils.formatFullDate(selectedDate, strings),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Text(
                text = DateUtils.formatShortDate(selectedDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }

            IconButton(
              onClick = { if (selectedDate < today) selectedDate += 1 },
              enabled = selectedDate < today,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .testTag("milk_next_day"),
            ) {
              Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "Next day")
            }
          }
        }

        // Daily Totals Card
        Card(
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = EmeraldPrimary),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = strings.totalYield,
                style = MaterialTheme.typography.labelLarge,
                color = PureWhite.copy(alpha = 0.85f),
              )
              Text(
                text = "${DateUtils.formatOneDecimal(dayTotal)} ${strings.litersUnit}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = PureWhite,
              )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              Column(horizontalAlignment = Alignment.End) {
                Text(strings.morningShift, style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.8f))
                Text(
                  text = "${DateUtils.formatOneDecimal(morningTotal)} ${strings.litersUnit}",
                  style = MaterialTheme.typography.titleSmall,
                  color = PureWhite,
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(strings.eveningShift, style = MaterialTheme.typography.labelSmall, color = PureWhite.copy(alpha = 0.8f))
                Text(
                  text = "${DateUtils.formatOneDecimal(eveningTotal)} ${strings.litersUnit}",
                  style = MaterialTheme.typography.titleSmall,
                  color = PureWhite,
                )
              }
            }
          }
        }

        // Shift Filter + Log Milk Action
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
              selected = shiftFilter == "ALL",
              onClick = { shiftFilter = "ALL" },
              label = { Text(strings.filterAll) },
            )
            FilterChip(
              selected = shiftFilter == "MORNING",
              onClick = { shiftFilter = "MORNING" },
              label = { Text(strings.morningShift) },
            )
            FilterChip(
              selected = shiftFilter == "EVENING",
              onClick = { shiftFilter = "EVENING" },
              label = { Text(strings.eveningShift) },
            )
          }
          Button(
            onClick = { onOpenLogMilk(selectedDate) },
            modifier = Modifier.testTag("log_milk_top_button"),
          ) {
            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(strings.logMilk, maxLines = 1)
          }
        }
      }

      if (dayRecords.isEmpty()) {
        Box(modifier = Modifier.padding(16.dp)) {
          EmptyStateCard(
            icon = Icons.Rounded.WaterDrop,
            title = strings.emptyMilkTitle,
            subtitle = strings.emptyMilkSubtitle,
            actionLabel = if (cattleList.isNotEmpty()) strings.logMilk else null,
            onAction = if (cattleList.isNotEmpty()) ({ onOpenLogMilk(selectedDate) }) else null,
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 92.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          items(dayRecords, key = { it.id }) { record ->
            val isMorning = record.shift == "MORNING"
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
                  color = if (isMorning) AmberContainer else EveningIndigoContainer,
                  modifier = Modifier.size(42.dp),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = if (isMorning) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay,
                      contentDescription = null,
                      tint = if (isMorning) WarmAmber else EveningIndigo,
                      modifier = Modifier.size(20.dp),
                    )
                  }
                }

                Column(
                  modifier = Modifier.weight(1f),
                  verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                  Text(
                    text = "${record.cowName} (${record.cowTagId})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                  val fatText = record.fatPercentage?.let { " • Fat ${DateUtils.formatOneDecimal(it)}%" } ?: ""
                  Text(
                    text = "${if (isMorning) strings.morningShift else strings.eveningShift}$fatText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                  if (record.notes.isNotBlank()) {
                    Text(
                      text = record.notes,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MintSoft,
                ) {
                  Text(
                    text = "${DateUtils.formatOneDecimal(record.liters)} ${strings.litersUnit}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldDark,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  )
                }

                if (userRole == UserRole.ADMIN) {
                  IconButton(
                    onClick = { onDeleteMilkRecord(record.id) },
                    modifier = Modifier.minimumInteractiveComponentSize(),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.DeleteOutline,
                      contentDescription = strings.delete,
                      tint = AlertCoral,
                      modifier = Modifier.size(20.dp),
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    if (cattleList.isNotEmpty()) {
      FloatingActionButton(
        onClick = { onOpenLogMilk(selectedDate) },
        containerColor = MilkSky,
        contentColor = PureWhite,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(18.dp)
          .testTag("fab_log_milk"),
      ) {
        Icon(Icons.Rounded.Add, contentDescription = strings.logMilk)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogMilkModalSheet(
  cattleList: List<CattleEntity>,
  preselectedCow: CattleEntity? = null,
  dateEpochDay: Long = DateUtils.todayEpochDay(),
  onSave: (CattleEntity, Long, String, Double, Double?, String) -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val milkingFirst = remember(cattleList) {
    cattleList.sortedByDescending { it.lactationStatus == "MILKING" }
  }
  var selectedCow by remember(preselectedCow, milkingFirst) {
    mutableStateOf(preselectedCow ?: milkingFirst.firstOrNull())
  }
  var shift by remember { mutableStateOf("MORNING") }
  var litersText by remember { mutableStateOf("") }
  var fatText by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
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
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Text(
        text = "${strings.logMilk} • ${DateUtils.formatFullDate(dateEpochDay, strings)}",
        style = MaterialTheme.typography.headlineSmall,
      )

      Text(
        text = strings.selectCow,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        milkingFirst.forEach { cow ->
          FilterChip(
            selected = selectedCow?.id == cow.id,
            onClick = { selectedCow = cow },
            label = { Text("${cow.name} (${cow.cowTagId})") },
          )
        }
      }

      Text(
        text = strings.shiftLabel,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FilterChip(
          selected = shift == "MORNING",
          onClick = { shift = "MORNING" },
          label = { Text(strings.morningShift) },
        )
        FilterChip(
          selected = shift == "EVENING",
          onClick = { shift = "EVENING" },
          label = { Text(strings.eveningShift) },
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OutlinedTextField(
          value = litersText,
          onValueChange = { litersText = it.filter { ch -> ch.isDigit() || ch == '.' } },
          label = { Text(strings.quantityLiters) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("milk_liters_input"),
        )
        OutlinedTextField(
          value = fatText,
          onValueChange = { fatText = it.filter { ch -> ch.isDigit() || ch == '.' } },
          label = { Text(strings.fatPercentOptional) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.weight(1f),
        )
      }

      OutlinedTextField(
        value = notes,
        onValueChange = { notes = it },
        label = { Text(strings.notesOptional) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
      )

      if (errorText != null) {
        Text(
          text = errorText ?: "",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.error,
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        TextButton(onClick = onDismiss) { Text(strings.cancel) }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = {
            val cow = selectedCow
            val liters = litersText.toDoubleOrNull()
            val fat = fatText.toDoubleOrNull()
            when {
              cow == null -> errorText = strings.validationRequired
              liters == null || liters <= 0.0 -> errorText = strings.validationPositiveNumber
              else -> onSave(cow, dateEpochDay, shift, liters, fat, notes)
            }
          },
          modifier = Modifier.testTag("save_milk_button"),
        ) {
          Text(strings.save)
        }
      }
    }
  }
}
