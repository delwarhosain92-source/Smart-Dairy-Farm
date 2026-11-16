package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Vaccines
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.CattleEntity
import com.example.data.local.HealthRecordEntity
import com.example.data.local.MilkRecordEntity
import com.example.data.preferences.UserRole
import com.example.ui.components.CattleAvatar
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.HealthStatusBadge
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.util.DateUtils

@Composable
fun CattleDetailScreen(
  cow: CattleEntity?,
  milkHistory: List<MilkRecordEntity>,
  healthHistory: List<HealthRecordEntity>,
  userRole: UserRole,
  onEditClick: (CattleEntity) -> Unit,
  onDeleteConfirm: (Long) -> Unit,
  onQuickLogMilk: (CattleEntity) -> Unit,
  onQuickAddHealth: (CattleEntity) -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val isAdmin = userRole == UserRole.ADMIN
  var showDeleteDialog by remember { mutableStateOf(false) }

  if (cow == null) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text(strings.emptyCattleTitle, style = MaterialTheme.typography.titleMedium)
    }
    return
  }

  val years = cow.ageMonths / 12
  val months = cow.ageMonths % 12
  val ageFormatted = buildString {
    if (years > 0) append("$years ${strings.yearsShort} ")
    if (months > 0 || years == 0) append("$months ${strings.monthsShort}")
  }.trim()
  val totalMilkLogged = milkHistory.sumOf { it.liters }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    LazyColumn(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // Profile Header Card
      item {
        Card(
          shape = MaterialTheme.shapes.large,
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
              CattleAvatar(
                photoUri = cow.photoUri,
                contentDescription = cow.name,
                size = 88.dp,
              )
              Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MintSoft,
                ) {
                  Text(
                    text = cow.cowTagId,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = EmeraldDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  )
                }
                Text(
                  text = cow.name,
                  style = MaterialTheme.typography.headlineSmall,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  HealthStatusBadge(
                    status = cow.healthStatus,
                    label = strings.healthStatusLabel(cow.healthStatus),
                  )
                  Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                  ) {
                    Text(
                      text = strings.lactationStatusLabel(cow.lactationStatus),
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                  }
                }
              }
            }

            // Vitals Grid
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              VitalBox(
                label = strings.breedLabel,
                value = strings.breedDisplay(cow.breed),
                modifier = Modifier.weight(1f),
              )
              VitalBox(
                label = strings.ageLabel,
                value = ageFormatted,
                modifier = Modifier.weight(1f),
              )
              VitalBox(
                label = strings.weightLabel,
                value = "${cow.weightKg.toInt()} ${strings.kgUnit}",
                modifier = Modifier.weight(1f),
              )
            }
            if (cow.dateOfBirthEpochDay != null) {
              VitalBox(
                label = strings.dateOfBirthLabel,
                value = DateUtils.formatDob(cow.dateOfBirthEpochDay),
                modifier = Modifier.fillMaxWidth(),
              )
            }

            if (cow.healthNotes.isNotBlank()) {
              Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(
                  modifier = Modifier.padding(12.dp),
                  verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                  Text(
                    text = strings.healthNotesLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                  Text(
                    text = cow.healthNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                }
              }
            }

            // Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Button(
                onClick = { onQuickLogMilk(cow) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("detail_log_milk_button"),
              ) {
                Icon(Icons.Rounded.WaterDrop, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.logMilk, maxLines = 1)
              }
              OutlinedButton(
                onClick = { onQuickAddHealth(cow) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("detail_add_health_button"),
              ) {
                Icon(Icons.Rounded.Vaccines, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.addHealthRecord, maxLines = 1)
              }
            }

            if (isAdmin) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                OutlinedButton(
                  onClick = { onEditClick(cow) },
                  modifier = Modifier.weight(1f),
                ) {
                  Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(strings.editCattle)
                }
                OutlinedButton(
                  onClick = { showDeleteDialog = true },
                  colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCoral),
                  border = BorderStroke(1.dp, AlertCoral.copy(alpha = 0.5f)),
                  modifier = Modifier.weight(1f),
                ) {
                  Icon(Icons.Rounded.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(strings.delete)
                }
              }
            }
          }
        }
      }

      // Milk History Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = strings.milkHistorySection,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
          )
          Text(
            text = "${DateUtils.formatOneDecimal(totalMilkLogged)} ${strings.litersUnit}",
            style = MaterialTheme.typography.labelLarge,
            color = EmeraldPrimary,
          )
        }
      }

      if (milkHistory.isEmpty()) {
        item {
          Text(
            text = strings.emptyMilkTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      } else {
        items(milkHistory.take(10), key = { "detail_milk_${it.id}" }) { milk ->
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
              Column {
                Text(
                  text = DateUtils.formatFullDate(milk.dateEpochDay, strings),
                  style = MaterialTheme.typography.titleSmall,
                )
                Text(
                  text = if (milk.shift == "MORNING") strings.morningShift else strings.eveningShift,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Text(
                text = "${DateUtils.formatOneDecimal(milk.liters)} ${strings.litersUnit}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = EmeraldPrimary,
              )
            }
          }
        }
      }

      // Health History Section
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = strings.healthHistorySection,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onBackground,
        )
      }

      if (healthHistory.isEmpty()) {
        item {
          Text(
            text = strings.emptyHealthTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      } else {
        items(healthHistory, key = { "detail_health_${it.id}" }) { rec ->
          Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Text(
                  text = rec.title,
                  style = MaterialTheme.typography.titleSmall,
                )
                Text(
                  text = if (rec.isCompleted) strings.completedBadge else strings.dueBadge,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (rec.isCompleted) EmeraldPrimary else AlertCoral,
                )
              }
              Text(
                text = "${strings.healthRecordTypeDisplay(rec.recordType)} • ${DateUtils.formatFullDate(rec.dateEpochDay, strings)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(16.dp)) }
    }
  }

  if (showDeleteDialog) {
    ConfirmDeleteDialog(
      title = "${strings.deleteCattle}: ${cow.name}",
      message = strings.confirmDeleteCattle,
      onConfirm = {
        showDeleteDialog = false
        onDeleteConfirm(cow.id)
      },
      onDismiss = { showDeleteDialog = false },
    )
  }
}

@Composable
private fun VitalBox(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.small,
    color = MintSoft.copy(alpha = 0.6f),
    modifier = modifier,
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall,
        color = EmeraldDark,
      )
    }
  }
}
