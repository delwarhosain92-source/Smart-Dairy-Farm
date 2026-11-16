package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.CattleEntity
import com.example.data.preferences.UserRole
import com.example.ui.components.CattleAvatar
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.HealthStatusBadge
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite

private enum class CattleFilter { ALL, HEALTHY, UNDER_TREATMENT, PREGNANT, MILKING }

@Composable
fun CattleScreen(
  cattleList: List<CattleEntity>,
  userRole: UserRole,
  onSelectCattle: (Long) -> Unit,
  onAddCattleClick: () -> Unit,
  onEditCattleClick: (CattleEntity) -> Unit,
  onDeleteCattle: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val isAdmin = userRole == UserRole.ADMIN
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf(CattleFilter.ALL) }
  var cattleToDelete by remember { mutableStateOf<CattleEntity?>(null) }

  val filteredCattle = remember(cattleList, searchQuery, selectedFilter) {
    cattleList.filter { cow ->
      val matchesQuery = searchQuery.isBlank() ||
        cow.name.contains(searchQuery, ignoreCase = true) ||
        cow.cowTagId.contains(searchQuery, ignoreCase = true) ||
        cow.breed.contains(searchQuery, ignoreCase = true)

      val matchesFilter = when (selectedFilter) {
        CattleFilter.ALL -> true
        CattleFilter.HEALTHY -> cow.healthStatus == "HEALTHY"
        CattleFilter.UNDER_TREATMENT -> cow.healthStatus == "UNDER_TREATMENT" || cow.healthStatus == "CHECKUP_DUE"
        CattleFilter.PREGNANT -> cow.healthStatus == "PREGNANT"
        CattleFilter.MILKING -> cow.lactationStatus == "MILKING"
      }
      matchesQuery && matchesFilter
    }
  }

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize(),
    ) {
      // Search & Filter Header
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(strings.searchCattleHint) },
            leadingIcon = {
              Icon(Icons.Rounded.Search, contentDescription = null, tint = EmeraldPrimary)
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Rounded.Clear, contentDescription = strings.close)
                }
              }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
              .weight(1f)
              .testTag("cattle_search_input"),
          )
          if (isAdmin) {
            Button(
              onClick = onAddCattleClick,
              modifier = Modifier
                .height(52.dp)
                .testTag("add_cattle_top_button"),
            ) {
              Icon(Icons.Rounded.Add, contentDescription = strings.addCattle, modifier = Modifier.size(18.dp))
            }
          }
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          CattleFilter.entries.forEach { filter ->
            val label = when (filter) {
              CattleFilter.ALL -> "${strings.filterAll} (${cattleList.size})"
              CattleFilter.HEALTHY -> strings.filterHealthy
              CattleFilter.UNDER_TREATMENT -> strings.filterTreatment
              CattleFilter.PREGNANT -> strings.filterPregnant
              CattleFilter.MILKING -> strings.filterMilking
            }
            FilterChip(
              selected = selectedFilter == filter,
              onClick = { selectedFilter = filter },
              label = { Text(label) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MintSoft,
                selectedLabelColor = EmeraldDark,
              ),
            )
          }
        }
      }

      if (filteredCattle.isEmpty()) {
        Box(modifier = Modifier.padding(16.dp)) {
          EmptyStateCard(
            icon = Icons.Rounded.Pets,
            title = strings.emptyCattleTitle,
            subtitle = strings.emptyCattleSubtitle,
            actionLabel = if (isAdmin) strings.addCattle else null,
            onAction = if (isAdmin) onAddCattleClick else null,
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("cattle_list"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 92.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          items(filteredCattle, key = { it.id }) { cow ->
            CattleListCard(
              cow = cow,
              isAdmin = isAdmin,
              onClick = { onSelectCattle(cow.id) },
              onEditClick = { onEditCattleClick(cow) },
              onDeleteClick = { cattleToDelete = cow },
            )
          }
        }
      }
    }

    if (isAdmin) {
      FloatingActionButton(
        onClick = onAddCattleClick,
        containerColor = EmeraldPrimary,
        contentColor = PureWhite,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(18.dp)
          .testTag("fab_add_cattle"),
      ) {
        Icon(Icons.Rounded.Add, contentDescription = strings.addCattle)
      }
    }
  }

  cattleToDelete?.let { target ->
    ConfirmDeleteDialog(
      title = "${strings.deleteCattle}: ${target.name}",
      message = strings.confirmDeleteCattle,
      onConfirm = {
        onDeleteCattle(target.id)
        cattleToDelete = null
      },
      onDismiss = { cattleToDelete = null },
    )
  }
}

@Composable
private fun CattleListCard(
  cow: CattleEntity,
  isAdmin: Boolean,
  onClick: () -> Unit,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit,
) {
  val strings = LocalAppStrings.current
  val years = cow.ageMonths / 12
  val months = cow.ageMonths % 12
  val ageText = buildString {
    if (years > 0) append("$years${strings.yearsShort} ")
    if (months > 0 || years == 0) append("$months${strings.monthsShort}")
  }.trim()

  Card(
    shape = MaterialTheme.shapes.medium,
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("cattle_card_${cow.cowTagId}"),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      CattleAvatar(
        photoUri = cow.photoUri,
        contentDescription = cow.name,
        size = 66.dp,
      )

      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MintSoft,
          ) {
            Text(
              text = cow.cowTagId,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = EmeraldDark,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            )
          }
          Text(
            text = cow.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }

        Text(
          text = "${strings.breedDisplay(cow.breed)} • $ageText • ${cow.weightKg.toInt()} ${strings.kgUnit}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
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
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
          }
        }
      }

      if (isAdmin) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          IconButton(
            onClick = onEditClick,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("edit_cattle_${cow.cowTagId}"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Edit,
              contentDescription = strings.editCattle,
              tint = EmeraldPrimary,
              modifier = Modifier.size(20.dp),
            )
          }
          IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("delete_cattle_${cow.cowTagId}"),
          ) {
            Icon(
              imageVector = Icons.Rounded.DeleteOutline,
              contentDescription = strings.deleteCattle,
              tint = AlertCoral,
              modifier = Modifier.size(20.dp),
            )
          }
        }
      }
    }
  }
}
