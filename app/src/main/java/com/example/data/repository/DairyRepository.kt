package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.CattleEntity
import com.example.data.local.DatabaseSeeder
import com.example.data.local.FeedItemEntity
import com.example.data.local.FeedLogEntity
import com.example.data.local.HealthRecordEntity
import com.example.data.local.MilkRecordEntity
import com.example.data.local.SmartDairyDatabase
import com.example.data.local.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class DairyRepository(
  private val context: Context,
  private val db: SmartDairyDatabase,
) {
  private val cattleDao = db.cattleDao()
  private val milkDao = db.milkDao()
  private val feedDao = db.feedDao()
  private val healthDao = db.healthDao()
  private val txDao = db.transactionDao()

  val allCattle: Flow<List<CattleEntity>> = cattleDao.observeAllCattle()
  val allMilkRecords: Flow<List<MilkRecordEntity>> = milkDao.observeAllMilkRecords()
  val allFeedItems: Flow<List<FeedItemEntity>> = feedDao.observeAllFeedItems()
  val recentFeedLogs: Flow<List<FeedLogEntity>> = feedDao.observeRecentFeedLogs()
  val allHealthRecords: Flow<List<HealthRecordEntity>> = healthDao.observeAllHealthRecords()
  val allTransactions: Flow<List<TransactionEntity>> = txDao.observeAllTransactions()

  fun observeCattleById(id: Long): Flow<CattleEntity?> = cattleDao.observeCattleById(id)
  fun observeMilkForCattle(id: Long): Flow<List<MilkRecordEntity>> = milkDao.observeMilkForCattle(id)
  fun observeHealthForCattle(id: Long): Flow<List<HealthRecordEntity>> = healthDao.observeHealthForCattle(id)

  suspend fun ensureInitialDataIfFirstLaunch(alreadySeeded: Boolean, markSeeded: suspend () -> Unit) {
    if (!alreadySeeded && cattleDao.countCattle() == 0) {
      DatabaseSeeder.seedSampleFarmData(db)
      markSeeded()
    }
  }

  suspend fun seedDemoData() {
    clearAllData()
    DatabaseSeeder.seedSampleFarmData(db)
  }

  suspend fun clearAllData() {
    milkDao.deleteAllMilkRecords()
    healthDao.deleteAllHealthRecords()
    feedDao.deleteAllFeedLogs()
    feedDao.deleteAllFeedItems()
    txDao.deleteAllTransactions()
    cattleDao.deleteAllCattle()
  }

  // Cattle CRUD
  suspend fun saveCattle(cattle: CattleEntity): Long {
    return if (cattle.id == 0L) {
      cattleDao.insertCattle(cattle)
    } else {
      cattleDao.updateCattle(cattle)
      cattle.id
    }
  }

  suspend fun deleteCattle(id: Long) = cattleDao.deleteCattleById(id)

  // Milk CRUD
  suspend fun saveMilkRecord(record: MilkRecordEntity) {
    if (record.id == 0L) milkDao.insertMilkRecord(record) else milkDao.updateMilkRecord(record)
  }

  suspend fun deleteMilkRecord(id: Long) = milkDao.deleteMilkRecordById(id)

  // Feed CRUD & Consumption/Restock
  suspend fun saveFeedItem(item: FeedItemEntity) {
    if (item.id == 0L) feedDao.insertFeedItem(item) else feedDao.updateFeedItem(item)
  }

  suspend fun deleteFeedItem(id: Long) = feedDao.deleteFeedItemById(id)

  suspend fun logFeedConsumption(
    feedItem: FeedItemEntity,
    quantityKg: Double,
    dateEpochDay: Long,
    notes: String,
    roleName: String,
  ) {
    val updatedStock = (feedItem.stockKg - quantityKg).coerceAtLeast(0.0)
    feedDao.updateFeedItem(
      feedItem.copy(
        stockKg = updatedStock,
        lastUpdated = System.currentTimeMillis(),
      )
    )
    feedDao.insertFeedLog(
      FeedLogEntity(
        feedItemId = feedItem.id,
        feedName = feedItem.name,
        actionType = "CONSUMED",
        quantityKg = quantityKg,
        dateEpochDay = dateEpochDay,
        notes = notes,
        recordedByRole = roleName,
      )
    )
  }

  suspend fun restockFeedItem(
    feedItem: FeedItemEntity,
    quantityKg: Double,
    dateEpochDay: Long,
    notes: String,
    roleName: String,
    alsoRecordExpense: Boolean,
  ) {
    feedDao.updateFeedItem(
      feedItem.copy(
        stockKg = feedItem.stockKg + quantityKg,
        lastUpdated = System.currentTimeMillis(),
      )
    )
    feedDao.insertFeedLog(
      FeedLogEntity(
        feedItemId = feedItem.id,
        feedName = feedItem.name,
        actionType = "RESTOCKED",
        quantityKg = quantityKg,
        dateEpochDay = dateEpochDay,
        notes = notes,
        recordedByRole = roleName,
      )
    )
    if (alsoRecordExpense && feedItem.costPerKg > 0.0) {
      txDao.insertTransaction(
        TransactionEntity(
          type = "EXPENSE",
          category = "FEED_PURCHASE",
          amount = quantityKg * feedItem.costPerKg,
          dateEpochDay = dateEpochDay,
          description = "${feedItem.name} (${quantityKg.toInt()} kg)",
        )
      )
    }
  }

  // Health CRUD
  suspend fun saveHealthRecord(record: HealthRecordEntity) {
    if (record.id == 0L) healthDao.insertHealthRecord(record) else healthDao.updateHealthRecord(record)
  }

  suspend fun toggleHealthCompleted(record: HealthRecordEntity) {
    healthDao.updateHealthRecord(record.copy(isCompleted = !record.isCompleted))
  }

  suspend fun deleteHealthRecord(id: Long) = healthDao.deleteHealthRecordById(id)

  // Transaction CRUD
  suspend fun saveTransaction(tx: TransactionEntity) {
    if (tx.id == 0L) txDao.insertTransaction(tx) else txDao.updateTransaction(tx)
  }

  suspend fun deleteTransaction(id: Long) = txDao.deleteTransactionById(id)

  /**
   * Copies a selected Photo Picker image into app-private internal storage so it remains
   * readable offline forever without requiring any storage permissions.
   */
  suspend fun saveCattlePhotoLocally(sourceUri: Uri): String? = withContext(Dispatchers.IO) {
    runCatching {
      val dir = File(context.filesDir, "cattle_photos").apply { if (!exists()) mkdirs() }
      val destFile = File(dir, "cow_${System.currentTimeMillis()}.jpg")
      context.contentResolver.openInputStream(sourceUri)?.use { input ->
        FileOutputStream(destFile).use { output ->
          input.copyTo(output)
        }
      }
      Uri.fromFile(destFile).toString()
    }.getOrNull()
  }
}
