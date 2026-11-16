package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CattleEntity
import com.example.data.local.FeedItemEntity
import com.example.data.local.FeedLogEntity
import com.example.data.local.HealthRecordEntity
import com.example.data.local.MilkRecordEntity
import com.example.data.local.SmartDairyDatabase
import com.example.data.local.TransactionEntity
import com.example.data.preferences.CurrencyOption
import com.example.data.preferences.FarmPreferences
import com.example.data.preferences.FarmSettings
import com.example.data.preferences.UserRole
import com.example.data.repository.DairyRepository
import com.example.ui.theme.AppLanguage
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DailyMilkSummary(
  val epochDay: Long,
  val dayLabel: String,
  val morningLiters: Double,
  val eveningLiters: Double,
) {
  val totalLiters: Double get() = morningLiters + eveningLiters
}

data class DairyUiState(
  val settings: FarmSettings = FarmSettings(),
  val cattle: List<CattleEntity> = emptyList(),
  val milkRecords: List<MilkRecordEntity> = emptyList(),
  val feedItems: List<FeedItemEntity> = emptyList(),
  val feedLogs: List<FeedLogEntity> = emptyList(),
  val healthRecords: List<HealthRecordEntity> = emptyList(),
  val transactions: List<TransactionEntity> = emptyList(),
  val todayMilkTotal: Double = 0.0,
  val todayMorningMilk: Double = 0.0,
  val todayEveningMilk: Double = 0.0,
  val weeklyMilkSeries: List<DailyMilkSummary> = emptyList(),
  val totalIncome: Double = 0.0,
  val totalExpense: Double = 0.0,
  val netBalance: Double = 0.0,
  val lowStockFeedCount: Int = 0,
  val dueHealthCount: Int = 0,
  val underTreatmentCount: Int = 0,
  val milkingCowsCount: Int = 0,
)

class DairyViewModel(application: Application) : AndroidViewModel(application) {
  private val preferences = FarmPreferences(application)
  private val database = SmartDairyDatabase.getInstance(application)
  private val repository = DairyRepository(application, database)

  init {
    viewModelScope.launch {
      val initialSettings = preferences.settingsFlow.first()
      repository.ensureInitialDataIfFirstLaunch(initialSettings.hasSeededDemo) {
        preferences.markDemoSeeded(true)
      }
    }
  }

  private val baseDataFlow = combine(
    preferences.settingsFlow,
    repository.allCattle,
    repository.allMilkRecords,
    repository.allFeedItems,
    repository.recentFeedLogs,
  ) { settings, cattle, milk, feedItems, feedLogs ->
    Tuple5(settings, cattle, milk, feedItems, feedLogs)
  }

  val uiState: StateFlow<DairyUiState> = combine(
    baseDataFlow,
    repository.allHealthRecords,
    repository.allTransactions,
  ) { base, health, transactions ->
    val (settings, cattle, milk, feedItems, feedLogs) = base
    val today = DateUtils.todayEpochDay()

    val todayMilk = milk.filter { it.dateEpochDay == today }
    val todayMorning = todayMilk.filter { it.shift == "MORNING" }.sumOf { it.liters }
    val todayEvening = todayMilk.filter { it.shift == "EVENING" }.sumOf { it.liters }

    val weeklySeries = (6 downTo 0).map { offset ->
      val day = today - offset
      val dayRecords = milk.filter { it.dateEpochDay == day }
      DailyMilkSummary(
        epochDay = day,
        dayLabel = DateUtils.formatDayShortLabel(day),
        morningLiters = dayRecords.filter { it.shift == "MORNING" }.sumOf { it.liters },
        eveningLiters = dayRecords.filter { it.shift == "EVENING" }.sumOf { it.liters },
      )
    }

    val income = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val expense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val lowFeedCount = feedItems.count { it.stockKg <= it.reorderThresholdKg }
    val dueHealthCount = health.count { !it.isCompleted && (it.nextDueEpochDay == null || it.nextDueEpochDay <= today + 7) }
    val treatmentCount = cattle.count { it.healthStatus == "UNDER_TREATMENT" || it.healthStatus == "CHECKUP_DUE" }
    val milkingCount = cattle.count { it.lactationStatus == "MILKING" }

    DairyUiState(
      settings = settings,
      cattle = cattle,
      milkRecords = milk,
      feedItems = feedItems,
      feedLogs = feedLogs,
      healthRecords = health,
      transactions = transactions,
      todayMilkTotal = todayMorning + todayEvening,
      todayMorningMilk = todayMorning,
      todayEveningMilk = todayEvening,
      weeklyMilkSeries = weeklySeries,
      totalIncome = income,
      totalExpense = expense,
      netBalance = income - expense,
      lowStockFeedCount = lowFeedCount,
      dueHealthCount = dueHealthCount,
      underTreatmentCount = treatmentCount,
      milkingCowsCount = milkingCount,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DairyUiState(),
  )

  fun observeCattleById(id: Long): Flow<CattleEntity?> = repository.observeCattleById(id)
  fun observeMilkForCattle(id: Long): Flow<List<MilkRecordEntity>> = repository.observeMilkForCattle(id)
  fun observeHealthForCattle(id: Long): Flow<List<HealthRecordEntity>> = repository.observeHealthForCattle(id)

  // Settings & Roles
  fun toggleLanguage() {
    val current = uiState.value.settings.language
    val next = if (current == AppLanguage.ENGLISH) AppLanguage.BANGLA else AppLanguage.ENGLISH
    viewModelScope.launch { preferences.setLanguage(next) }
  }

  fun setLanguage(language: AppLanguage) {
    viewModelScope.launch { preferences.setLanguage(language) }
  }

  fun setUserRole(role: UserRole) {
    viewModelScope.launch { preferences.setUserRole(role) }
  }

  fun setCurrency(currency: CurrencyOption) {
    viewModelScope.launch { preferences.setCurrency(currency) }
  }

  fun setFarmName(name: String) {
    viewModelScope.launch { preferences.setFarmName(name) }
  }

  fun loadSampleData() {
    viewModelScope.launch {
      repository.seedDemoData()
      preferences.markDemoSeeded(true)
    }
  }

  fun clearAllData() {
    viewModelScope.launch {
      repository.clearAllData()
      preferences.markDemoSeeded(true)
    }
  }

  // Cattle Actions
  fun saveCattle(cattle: CattleEntity, pickedPhotoUri: Uri?) {
    viewModelScope.launch {
      val persistedPhoto = if (pickedPhotoUri != null) {
        repository.saveCattlePhotoLocally(pickedPhotoUri) ?: cattle.photoUri
      } else {
        cattle.photoUri
      }
      repository.saveCattle(cattle.copy(photoUri = persistedPhoto))
    }
  }

  fun deleteCattle(id: Long) {
    viewModelScope.launch { repository.deleteCattle(id) }
  }

  // Milk Actions
  fun saveMilkRecord(
    existingId: Long = 0L,
    cattle: CattleEntity,
    dateEpochDay: Long,
    shift: String,
    liters: Double,
    fatPercentage: Double?,
    notes: String,
  ) {
    viewModelScope.launch {
      repository.saveMilkRecord(
        MilkRecordEntity(
          id = existingId,
          cattleId = cattle.id,
          cowTagId = cattle.cowTagId,
          cowName = cattle.name,
          dateEpochDay = dateEpochDay,
          shift = shift,
          liters = liters,
          fatPercentage = fatPercentage,
          notes = notes.trim(),
          recordedByRole = uiState.value.settings.userRole.key,
        )
      )
    }
  }

  fun deleteMilkRecord(id: Long) {
    viewModelScope.launch { repository.deleteMilkRecord(id) }
  }

  // Feed Actions
  fun saveFeedItem(item: FeedItemEntity) {
    viewModelScope.launch { repository.saveFeedItem(item) }
  }

  fun deleteFeedItem(id: Long) {
    viewModelScope.launch { repository.deleteFeedItem(id) }
  }

  fun logFeedConsumption(feedItem: FeedItemEntity, quantityKg: Double, notes: String) {
    viewModelScope.launch {
      repository.logFeedConsumption(
        feedItem = feedItem,
        quantityKg = quantityKg,
        dateEpochDay = DateUtils.todayEpochDay(),
        notes = notes.trim(),
        roleName = uiState.value.settings.userRole.key,
      )
    }
  }

  fun restockFeedItem(
    feedItem: FeedItemEntity,
    quantityKg: Double,
    notes: String,
    alsoRecordExpense: Boolean,
  ) {
    viewModelScope.launch {
      repository.restockFeedItem(
        feedItem = feedItem,
        quantityKg = quantityKg,
        dateEpochDay = DateUtils.todayEpochDay(),
        notes = notes.trim(),
        roleName = uiState.value.settings.userRole.key,
        alsoRecordExpense = alsoRecordExpense,
      )
    }
  }

  // Health & Vaccine Actions
  fun saveHealthRecord(
    existingId: Long = 0L,
    cattle: CattleEntity,
    recordType: String,
    title: String,
    dateEpochDay: Long,
    nextDueEpochDay: Long?,
    vetName: String,
    cost: Double,
    notes: String,
    isCompleted: Boolean,
  ) {
    viewModelScope.launch {
      repository.saveHealthRecord(
        HealthRecordEntity(
          id = existingId,
          cattleId = cattle.id,
          cowTagId = cattle.cowTagId,
          cowName = cattle.name,
          recordType = recordType,
          title = title.trim(),
          dateEpochDay = dateEpochDay,
          nextDueEpochDay = nextDueEpochDay,
          vetName = vetName.trim(),
          cost = cost,
          notes = notes.trim(),
          isCompleted = isCompleted,
        )
      )
    }
  }

  fun toggleHealthCompleted(record: HealthRecordEntity) {
    viewModelScope.launch { repository.toggleHealthCompleted(record) }
  }

  fun deleteHealthRecord(id: Long) {
    viewModelScope.launch { repository.deleteHealthRecord(id) }
  }

  // Finance Actions
  fun saveTransaction(tx: TransactionEntity) {
    viewModelScope.launch { repository.saveTransaction(tx) }
  }

  fun deleteTransaction(id: Long) {
    viewModelScope.launch { repository.deleteTransaction(id) }
  }
}

private data class Tuple5<A, B, C, D, E>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
)
