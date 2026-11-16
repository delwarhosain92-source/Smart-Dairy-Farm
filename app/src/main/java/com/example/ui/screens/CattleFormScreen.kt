package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.ui.components.CattleAvatar
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite
import com.example.util.DateUtils

private val COMMON_BREEDS = listOf(
  "Holstein Friesian",
  "Sahiwal",
  "Jersey",
  "Red Chittagong",
  "Crossbred",
)

private val HEALTH_STATUSES = listOf("HEALTHY", "UNDER_TREATMENT", "PREGNANT", "CHECKUP_DUE")
private val MILK_STATUSES = listOf("MILKING", "DRY", "HEIFER")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CattleFormScreen(
  existingCattle: CattleEntity?,
  onSave: (CattleEntity, Uri?) -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val strings = LocalAppStrings.current
  val defaultDob = remember(existingCattle) {
    existingCattle?.dateOfBirthEpochDay
      ?: DateUtils.dobFromAgeMonths(existingCattle?.ageMonths ?: 24)
  }

  var cowTagId by remember(existingCattle) { mutableStateOf(existingCattle?.cowTagId ?: "") }
  var name by remember(existingCattle) { mutableStateOf(existingCattle?.name ?: "") }
  var breed by remember(existingCattle) { mutableStateOf(existingCattle?.breed ?: COMMON_BREEDS.first()) }
  var dobEpochDay by remember(existingCattle) { mutableLongStateOf(defaultDob) }
  var weightKg by remember(existingCattle) {
    mutableStateOf(existingCattle?.weightKg?.toInt()?.toString() ?: "400")
  }
  var healthStatus by remember(existingCattle) {
    mutableStateOf(existingCattle?.healthStatus ?: "HEALTHY")
  }
  var milkStatus by remember(existingCattle) {
    mutableStateOf(existingCattle?.lactationStatus ?: "MILKING")
  }
  var healthNotes by remember(existingCattle) {
    mutableStateOf(existingCattle?.healthNotes ?: "")
  }
  var pickedPhotoUri by remember { mutableStateOf<Uri?>(null) }
  var errorText by remember { mutableStateOf<String?>(null) }
  var showDatePicker by remember { mutableStateOf(false) }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri -> if (uri != null) pickedPhotoUri = uri },
  )

  val computedAgeMonths = remember(dobEpochDay) { DateUtils.calculateAgeMonths(dobEpochDay) }
  val ageYears = computedAgeMonths / 12
  val ageRemMonths = computedAgeMonths % 12
  val ageSummary = buildString {
    if (ageYears > 0) append("$ageYears${strings.yearsShort} ")
    if (ageRemMonths > 0 || ageYears == 0) append("$ageRemMonths${strings.monthsShort}")
  }.trim()

  Box(
    modifier = modifier
      .fillMaxSize()
      .imePadding()
      .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 720.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          Text(
            text = if (existingCattle == null) strings.addCattle else strings.editCattle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
          )

          // Photo Picker Row
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
          ) {
            CattleAvatar(
              photoUri = pickedPhotoUri?.toString() ?: existingCattle?.photoUri,
              contentDescription = strings.selectPhoto,
              size = 76.dp,
              modifier = Modifier.clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
            )
            OutlinedButton(
              onClick = {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              },
              modifier = Modifier.testTag("pick_cattle_photo_button"),
            ) {
              Icon(Icons.Rounded.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                if (pickedPhotoUri != null || existingCattle?.photoUri != null) strings.changePhoto
                else strings.selectPhoto
              )
            }
          }

          // Cow ID & Name
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            OutlinedTextField(
              value = cowTagId,
              onValueChange = { cowTagId = it },
              label = { Text(strings.cowIdLabel) },
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("cattle_id_input"),
            )
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text(strings.cowNameLabel) },
              singleLine = true,
              modifier = Modifier
                .weight(1f)
                .testTag("cattle_name_input"),
            )
          }

          // Breed
          Text(
            text = strings.breedLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            COMMON_BREEDS.forEach { option ->
              FilterChip(
                selected = breed == option,
                onClick = { breed = option },
                label = { Text(strings.breedDisplay(option)) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MintSoft,
                  selectedLabelColor = EmeraldDark,
                ),
              )
            }
          }
          OutlinedTextField(
            value = breed,
            onValueChange = { breed = it },
            label = { Text(strings.breedLabel) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("cattle_breed_input"),
          )

          // Date of Birth & Weight
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
          ) {
            Surface(
              shape = MaterialTheme.shapes.small,
              color = MaterialTheme.colorScheme.surface,
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
              modifier = Modifier
                .weight(1.2f)
                .clickable { showDatePicker = true }
                .testTag("cattle_dob_button"),
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = strings.dateOfBirthLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                  Text(
                    text = DateUtils.formatDob(dobEpochDay),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                  )
                  Text(
                    text = "${strings.ageLabel}: $ageSummary",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldPrimary,
                  )
                }
                Icon(
                  imageVector = Icons.Rounded.CalendarMonth,
                  contentDescription = strings.dateOfBirthLabel,
                  tint = EmeraldPrimary,
                )
              }
            }

            OutlinedTextField(
              value = weightKg,
              onValueChange = { weightKg = it.filter { ch -> ch.isDigit() || ch == '.' } },
              label = { Text(strings.weightKgLabel) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              singleLine = true,
              modifier = Modifier
                .weight(0.9f)
                .testTag("cattle_weight_input"),
            )
          }

          // Health Status
          Text(
            text = strings.healthStatusLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            HEALTH_STATUSES.forEach { status ->
              FilterChip(
                selected = healthStatus == status,
                onClick = { healthStatus = status },
                label = { Text(strings.healthStatusLabel(status)) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MintSoft,
                  selectedLabelColor = EmeraldDark,
                ),
                modifier = Modifier.testTag("health_chip_$status"),
              )
            }
          }

          // Milk Status
          Text(
            text = strings.lactationStatusLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            MILK_STATUSES.forEach { stage ->
              FilterChip(
                selected = milkStatus == stage,
                onClick = { milkStatus = stage },
                label = { Text(strings.lactationStatusLabel(stage)) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MintSoft,
                  selectedLabelColor = EmeraldDark,
                ),
                modifier = Modifier.testTag("milk_status_chip_$stage"),
              )
            }
          }

          // Health Notes
          OutlinedTextField(
            value = healthNotes,
            onValueChange = { healthNotes = it },
            label = { Text(strings.healthNotesLabel) },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("cattle_notes_input"),
          )

          if (errorText != null) {
            Text(
              text = errorText ?: "",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.error,
              modifier = Modifier.testTag("cattle_form_error"),
            )
          }

          // Cancel & Save Buttons
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            OutlinedButton(
              onClick = onCancel,
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("cancel_cattle_button"),
            ) {
              Text(strings.cancel)
            }
            Button(
              onClick = {
                val weight = weightKg.toDoubleOrNull()
                when {
                  cowTagId.isBlank() || name.isBlank() || breed.isBlank() -> {
                    errorText = strings.validationRequired
                  }
                  weight == null || weight <= 0.0 -> {
                    errorText = strings.validationPositiveNumber
                  }
                  else -> {
                    onSave(
                      CattleEntity(
                        id = existingCattle?.id ?: 0L,
                        cowTagId = cowTagId.trim().uppercase(),
                        name = name.trim(),
                        breed = breed.trim(),
                        ageMonths = computedAgeMonths,
                        dateOfBirthEpochDay = dobEpochDay,
                        weightKg = weight,
                        photoUri = existingCattle?.photoUri,
                        healthStatus = healthStatus,
                        lactationStatus = milkStatus,
                        healthNotes = healthNotes.trim(),
                        createdAt = existingCattle?.createdAt ?: System.currentTimeMillis(),
                      ),
                      pickedPhotoUri,
                    )
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = PureWhite,
              ),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("save_cattle_button"),
            ) {
              Text(strings.save)
            }
          }
        }
      }
    }
  }

  if (showDatePicker) {
    val todayMillis = DateUtils.epochDayToUtcPickerMillis(DateUtils.todayEpochDay())
    val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = DateUtils.epochDayToUtcPickerMillis(dobEpochDay),
      selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayMillis
      },
    )
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            datePickerState.selectedDateMillis?.let { millis ->
              dobEpochDay = DateUtils.utcPickerMillisToEpochDay(millis)
            }
            showDatePicker = false
          },
        ) {
          Text(strings.confirm)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text(strings.cancel)
        }
      },
    ) {
      DatePicker(state = datePickerState)
    }
  }
}
