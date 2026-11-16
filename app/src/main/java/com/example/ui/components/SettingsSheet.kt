package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.preferences.CurrencyOption
import com.example.data.preferences.FarmSettings
import com.example.data.preferences.UserRole
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintBorder
import com.example.ui.theme.MintSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmSettingsSheet(
  settings: FarmSettings,
  onSelectRole: (UserRole) -> Unit,
  onSelectLanguage: (com.example.ui.theme.AppLanguage) -> Unit,
  onSelectCurrency: (CurrencyOption) -> Unit,
  onSaveFarmName: (String) -> Unit,
  onLoadSampleData: () -> Unit,
  onClearAllData: () -> Unit,
  onDismiss: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var farmNameDraft by remember(settings.farmName) { mutableStateOf(settings.farmName) }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }

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
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = strings.settingsTitle,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
      )

      // Active User Role Selector
      Text(
        text = strings.switchRole,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        RoleOptionCard(
          title = strings.roleAdmin,
          description = strings.adminRoleDesc,
          icon = Icons.Rounded.AdminPanelSettings,
          selected = settings.userRole == UserRole.ADMIN,
          onClick = { onSelectRole(UserRole.ADMIN) },
          modifier = Modifier
            .weight(1f)
            .testTag("select_role_admin"),
        )
        RoleOptionCard(
          title = strings.roleFarmer,
          description = strings.farmerRoleDesc,
          icon = Icons.Rounded.Agriculture,
          selected = settings.userRole == UserRole.FARMER,
          onClick = { onSelectRole(UserRole.FARMER) },
          modifier = Modifier
            .weight(1f)
            .testTag("select_role_farmer"),
        )
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

      // Language Selector
      Text(
        text = strings.languageLabel,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        com.example.ui.theme.AppLanguage.entries.forEach { lang ->
          val selected = settings.language == lang
          Surface(
            shape = MaterialTheme.shapes.small,
            color = if (selected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .weight(1f)
              .clickable { onSelectLanguage(lang) }
              .testTag("select_lang_${lang.code}"),
          ) {
            Text(
              text = lang.label,
              style = MaterialTheme.typography.labelLarge,
              color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            )
          }
        }
      }

      // Currency Selector
      Text(
        text = strings.currencyLabel,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        CurrencyOption.entries.forEach { curr ->
          val selected = settings.currency == curr
          Surface(
            shape = MaterialTheme.shapes.small,
            color = if (selected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .weight(1f)
              .clickable { onSelectCurrency(curr) }
              .testTag("select_currency_${curr.code}"),
          ) {
            Text(
              text = "${curr.symbol}  ${curr.code}",
              style = MaterialTheme.typography.labelLarge,
              color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            )
          }
        }
      }

      // Farm Name Editor
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        OutlinedTextField(
          value = farmNameDraft,
          onValueChange = { farmNameDraft = it },
          label = { Text(strings.farmNameLabel) },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("farm_name_input"),
        )
        Button(
          onClick = {
            onSaveFarmName(farmNameDraft)
            onDismiss()
          },
          modifier = Modifier.height(54.dp),
        ) {
          Text(strings.save)
        }
      }

      // Offline Data Management (Admin only for reset/clear)
      if (settings.userRole == UserRole.ADMIN) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Text(
          text = strings.dataManagement,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = {
              onLoadSampleData()
              feedbackMessage = strings.sampleDataLoadedMsg
            },
            modifier = Modifier.weight(1f),
          ) {
            Icon(Icons.Rounded.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text(strings.loadSampleData, maxLines = 1)
          }
          OutlinedButton(
            onClick = {
              onClearAllData()
              feedbackMessage = strings.allDataClearedMsg
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCoral),
            border = BorderStroke(1.dp, AlertCoral.copy(alpha = 0.5f)),
            modifier = Modifier.weight(1f),
          ) {
            Icon(Icons.Rounded.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(6.dp))
            Text(strings.clearAllData, maxLines = 1)
          }
        }
      }

      if (feedbackMessage != null) {
        Text(
          text = feedbackMessage ?: "",
          style = MaterialTheme.typography.bodySmall,
          color = EmeraldPrimary,
        )
      }
    }
  }
}

@Composable
private fun RoleOptionCard(
  title: String,
  description: String,
  icon: ImageVector,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.medium,
    color = if (selected) MintSoft else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    border = BorderStroke(
      width = if (selected) 1.5.dp else 1.dp,
      color = if (selected) EmeraldPrimary else MintBorder,
    ),
    modifier = modifier.clickable(onClick = onClick),
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = if (selected) EmeraldDark else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp),
        )
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          color = if (selected) EmeraldDark else MaterialTheme.colorScheme.onSurface,
        )
      }
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
