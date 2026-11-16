package com.example.data.local

import androidx.room.withTransaction
import com.example.util.DateUtils

object DatabaseSeeder {
  suspend fun seedSampleFarmData(db: SmartDairyDatabase) = db.withTransaction {
    val today = DateUtils.todayEpochDay()
    val cattleDao = db.cattleDao()
    val milkDao = db.milkDao()
    val feedDao = db.feedDao()
    val healthDao = db.healthDao()
    val txDao = db.transactionDao()

    val cow1Id = cattleDao.insertCattle(
      CattleEntity(
        cowTagId = "COW-101",
        name = "Lakshmi",
        breed = "Holstein Friesian",
        ageMonths = 42,
        weightKg = 540.0,
        healthStatus = "HEALTHY",
        lactationStatus = "MILKING",
        healthNotes = "High-yielding milking cow, steady appetite and normal body temperature.",
      )
    )
    val cow2Id = cattleDao.insertCattle(
      CattleEntity(
        cowTagId = "COW-102",
        name = "Shyama",
        breed = "Sahiwal",
        ageMonths = 36,
        weightKg = 435.0,
        healthStatus = "HEALTHY",
        lactationStatus = "MILKING",
        healthNotes = "Calm temperament, good fat percentage in evening milk.",
      )
    )
    val cow3Id = cattleDao.insertCattle(
      CattleEntity(
        cowTagId = "COW-103",
        name = "Gouri",
        breed = "Jersey",
        ageMonths = 48,
        weightKg = 410.0,
        healthStatus = "PREGNANT",
        lactationStatus = "DRY",
        healthNotes = "7 months pregnant, shifted to dry-cow mineral ration.",
      )
    )
    val cow4Id = cattleDao.insertCattle(
      CattleEntity(
        cowTagId = "COW-104",
        name = "Kajoli",
        breed = "Red Chittagong",
        ageMonths = 28,
        weightKg = 315.0,
        healthStatus = "HEALTHY",
        lactationStatus = "MILKING",
        healthNotes = "First lactation indigenous breed, disease-resistant and active.",
      )
    )
    val cow5Id = cattleDao.insertCattle(
      CattleEntity(
        cowTagId = "COW-105",
        name = "Rani",
        breed = "Crossbred",
        ageMonths = 52,
        weightKg = 490.0,
        healthStatus = "UNDER_TREATMENT",
        lactationStatus = "MILKING",
        healthNotes = "Mild hoof inflammation on left hind leg; antibiotic ointment applied.",
      )
    )

    val cows = listOf(
      Triple(cow1Id, "COW-101", "Lakshmi") to 12.5,
      Triple(cow2Id, "COW-102", "Shyama") to 9.0,
      Triple(cow4Id, "COW-104", "Kajoli") to 6.5,
      Triple(cow5Id, "COW-105", "Rani") to 8.0,
    )

    // Seed 7 days of milk records
    for (offset in 6 downTo 0) {
      val day = today - offset
      cows.forEachIndexed { idx, (cowTriple, baseMorning) ->
        val (cid, tag, name) = cowTriple
        val variation = ((offset + idx) % 3) * 0.4 - 0.3
        milkDao.insertMilkRecord(
          MilkRecordEntity(
            cattleId = cid,
            cowTagId = tag,
            cowName = name,
            dateEpochDay = day,
            shift = "MORNING",
            liters = (baseMorning + variation).coerceAtLeast(3.0),
            fatPercentage = 4.1 + (idx * 0.2),
            notes = if (offset == 0) "Clean morning yield" else "",
            recordedByRole = "FARMER",
          )
        )
        milkDao.insertMilkRecord(
          MilkRecordEntity(
            cattleId = cid,
            cowTagId = tag,
            cowName = name,
            dateEpochDay = day,
            shift = "EVENING",
            liters = (baseMorning * 0.78 + variation).coerceAtLeast(2.5),
            fatPercentage = 4.3 + (idx * 0.2),
            notes = "",
            recordedByRole = "FARMER",
          )
        )
      }
    }

    // Seed Feed Inventory
    val f1 = feedDao.insertFeedItem(
      FeedItemEntity(
        name = "Napier Green Grass",
        category = "GREEN_FODDER",
        stockKg = 420.0,
        dailyUsageKg = 95.0,
        reorderThresholdKg = 150.0,
        costPerKg = 4.5,
      )
    )
    val f2 = feedDao.insertFeedItem(
      FeedItemEntity(
        name = "Dairy Concentrate Mix",
        category = "CONCENTRATE",
        stockKg = 75.0,
        dailyUsageKg = 24.0,
        reorderThresholdKg = 100.0, // Low stock trigger!
        costPerKg = 46.0,
      )
    )
    feedDao.insertFeedItem(
      FeedItemEntity(
        name = "Maize Silage",
        category = "SILAGE",
        stockKg = 640.0,
        dailyUsageKg = 55.0,
        reorderThresholdKg = 150.0,
        costPerKg = 9.0,
      )
    )
    feedDao.insertFeedItem(
      FeedItemEntity(
        name = "Rice Straw (Khar)",
        category = "DRY_ROUGHAGE",
        stockKg = 310.0,
        dailyUsageKg = 30.0,
        reorderThresholdKg = 80.0,
        costPerKg = 7.0,
      )
    )

    feedDao.insertFeedLog(
      FeedLogEntity(
        feedItemId = f1,
        feedName = "Napier Green Grass",
        actionType = "CONSUMED",
        quantityKg = 95.0,
        dateEpochDay = today,
        notes = "Morning & afternoon herd ration",
        recordedByRole = "FARMER",
      )
    )
    feedDao.insertFeedLog(
      FeedLogEntity(
        feedItemId = f2,
        feedName = "Dairy Concentrate Mix",
        actionType = "CONSUMED",
        quantityKg = 24.0,
        dateEpochDay = today,
        notes = "Milking cows concentrate ration",
        recordedByRole = "FARMER",
      )
    )

    // Seed Health & Vaccination Records
    healthDao.insertHealthRecord(
      HealthRecordEntity(
        cattleId = cow1Id,
        cowTagId = "COW-101",
        cowName = "Lakshmi",
        recordType = "VACCINATION",
        title = "FMD Booster Vaccine",
        dateEpochDay = today - 25,
        nextDueEpochDay = today + 3,
        vetName = "Dr. Rafiqul Islam",
        cost = 450.0,
        notes = "Annual foot-and-mouth booster due in 3 days",
        isCompleted = false,
      )
    )
    healthDao.insertHealthRecord(
      HealthRecordEntity(
        cattleId = cow5Id,
        cowTagId = "COW-105",
        cowName = "Rani",
        recordType = "TREATMENT",
        title = "Hoof Inflammation Care",
        dateEpochDay = today - 2,
        nextDueEpochDay = today,
        vetName = "Dr. Tania Sultana",
        cost = 850.0,
        notes = "Antiseptic footbath and follow-up dressing due today",
        isCompleted = false,
      )
    )
    healthDao.insertHealthRecord(
      HealthRecordEntity(
        cattleId = cow3Id,
        cowTagId = "COW-103",
        cowName = "Gouri",
        recordType = "CHECKUP",
        title = "7th Month Pregnancy Checkup",
        dateEpochDay = today - 5,
        nextDueEpochDay = today + 25,
        vetName = "Dr. Rafiqul Islam",
        cost = 600.0,
        notes = "Fetus heartbeat normal; calcium supplement advised",
        isCompleted = true,
      )
    )
    healthDao.insertHealthRecord(
      HealthRecordEntity(
        cattleId = cow2Id,
        cowTagId = "COW-102",
        cowName = "Shyama",
        recordType = "DEWORMING",
        title = "Albendazole Oral Dewormer",
        dateEpochDay = today - 14,
        nextDueEpochDay = today + 76,
        vetName = "Dr. Tania Sultana",
        cost = 250.0,
        notes = "Completed on schedule",
        isCompleted = true,
      )
    )

    // Seed Income & Expense Transactions
    txDao.insertTransaction(
      TransactionEntity(
        type = "INCOME",
        category = "MILK_SALE",
        amount = 14800.0,
        dateEpochDay = today,
        description = "Daily wholesale & retail milk supply (6 days cycle)",
      )
    )
    txDao.insertTransaction(
      TransactionEntity(
        type = "INCOME",
        category = "MILK_SALE",
        amount = 12400.0,
        dateEpochDay = today - 5,
        description = "Local dairy chilling center payment",
      )
    )
    txDao.insertTransaction(
      TransactionEntity(
        type = "INCOME",
        category = "MANURE_SALE",
        amount = 3200.0,
        dateEpochDay = today - 3,
        description = "Organic compost bags sold to nearby vegetable farm",
      )
    )
    txDao.insertTransaction(
      TransactionEntity(
        type = "EXPENSE",
        category = "FEED_PURCHASE",
        amount = 7600.0,
        dateEpochDay = today - 4,
        description = "Maize silage & wheat bran stock purchase",
      )
    )
    txDao.insertTransaction(
      TransactionEntity(
        type = "EXPENSE",
        category = "VET_MEDICINE",
        amount = 1900.0,
        dateEpochDay = today - 2,
        description = "Vet consultation, FMD vials & hoof medicine",
      )
    )
    txDao.insertTransaction(
      TransactionEntity(
        type = "EXPENSE",
        category = "LABOR",
        amount = 6500.0,
        dateEpochDay = today - 6,
        description = "Weekly shed caretaker & milking staff wages",
      )
    )
    txDao.insertTransaction(
      TransactionEntity(
        type = "EXPENSE",
        category = "UTILITIES",
        amount = 1450.0,
        dateEpochDay = today - 1,
        description = "Chaff cutter electricity & water pump maintenance",
      )
    )
  }
}
