package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cattle")
data class CattleEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cowTagId: String,
  val name: String,
  val breed: String,
  val ageMonths: Int,
  val dateOfBirthEpochDay: Long? = null,
  val weightKg: Double,
  val photoUri: String? = null,
  val healthStatus: String = "HEALTHY",
  val lactationStatus: String = "MILKING",
  val healthNotes: String = "",
  val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "milk_records")
data class MilkRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cattleId: Long,
  val cowTagId: String,
  val cowName: String,
  val dateEpochDay: Long,
  val shift: String, // MORNING or EVENING
  val liters: Double,
  val fatPercentage: Double? = null,
  val notes: String = "",
  val recordedByRole: String = "FARMER",
  val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "feed_items")
data class FeedItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val category: String, // GREEN_FODDER, DRY_ROUGHAGE, CONCENTRATE, SILAGE, MINERAL_SUPPLEMENT
  val stockKg: Double,
  val dailyUsageKg: Double,
  val reorderThresholdKg: Double,
  val costPerKg: Double,
  val lastUpdated: Long = System.currentTimeMillis(),
)

@Entity(tableName = "feed_logs")
data class FeedLogEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val feedItemId: Long,
  val feedName: String,
  val actionType: String, // CONSUMED or RESTOCKED
  val quantityKg: Double,
  val dateEpochDay: Long,
  val notes: String = "",
  val recordedByRole: String = "FARMER",
  val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "health_records")
data class HealthRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cattleId: Long,
  val cowTagId: String,
  val cowName: String,
  val recordType: String, // VACCINATION, TREATMENT, DEWORMING, CHECKUP
  val title: String,
  val dateEpochDay: Long,
  val nextDueEpochDay: Long? = null,
  val vetName: String = "",
  val cost: Double = 0.0,
  val notes: String = "",
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val type: String, // INCOME or EXPENSE
  val category: String,
  val amount: Double,
  val dateEpochDay: Long,
  val description: String,
  val createdAt: Long = System.currentTimeMillis(),
)
