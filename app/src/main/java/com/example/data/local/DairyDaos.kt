package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CattleDao {
  @Query("SELECT * FROM cattle ORDER BY createdAt DESC, id DESC")
  fun observeAllCattle(): Flow<List<CattleEntity>>

  @Query("SELECT * FROM cattle WHERE id = :id")
  fun observeCattleById(id: Long): Flow<CattleEntity?>

  @Query("SELECT COUNT(*) FROM cattle")
  suspend fun countCattle(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCattle(cattle: CattleEntity): Long

  @Update
  suspend fun updateCattle(cattle: CattleEntity)

  @Query("DELETE FROM cattle WHERE id = :id")
  suspend fun deleteCattleById(id: Long)

  @Query("DELETE FROM cattle")
  suspend fun deleteAllCattle()
}

@Dao
interface MilkDao {
  @Query("SELECT * FROM milk_records ORDER BY dateEpochDay DESC, createdAt DESC")
  fun observeAllMilkRecords(): Flow<List<MilkRecordEntity>>

  @Query("SELECT * FROM milk_records WHERE cattleId = :cattleId ORDER BY dateEpochDay DESC, createdAt DESC")
  fun observeMilkForCattle(cattleId: Long): Flow<List<MilkRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMilkRecord(record: MilkRecordEntity): Long

  @Update
  suspend fun updateMilkRecord(record: MilkRecordEntity)

  @Query("DELETE FROM milk_records WHERE id = :id")
  suspend fun deleteMilkRecordById(id: Long)

  @Query("DELETE FROM milk_records")
  suspend fun deleteAllMilkRecords()
}

@Dao
interface FeedDao {
  @Query("SELECT * FROM feed_items ORDER BY name ASC")
  fun observeAllFeedItems(): Flow<List<FeedItemEntity>>

  @Query("SELECT * FROM feed_logs ORDER BY dateEpochDay DESC, createdAt DESC LIMIT 50")
  fun observeRecentFeedLogs(): Flow<List<FeedLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFeedItem(item: FeedItemEntity): Long

  @Update
  suspend fun updateFeedItem(item: FeedItemEntity)

  @Query("DELETE FROM feed_items WHERE id = :id")
  suspend fun deleteFeedItemById(id: Long)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFeedLog(log: FeedLogEntity): Long

  @Query("DELETE FROM feed_logs")
  suspend fun deleteAllFeedLogs()

  @Query("DELETE FROM feed_items")
  suspend fun deleteAllFeedItems()
}

@Dao
interface HealthDao {
  @Query("SELECT * FROM health_records ORDER BY isCompleted ASC, dateEpochDay DESC, createdAt DESC")
  fun observeAllHealthRecords(): Flow<List<HealthRecordEntity>>

  @Query("SELECT * FROM health_records WHERE cattleId = :cattleId ORDER BY dateEpochDay DESC")
  fun observeHealthForCattle(cattleId: Long): Flow<List<HealthRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHealthRecord(record: HealthRecordEntity): Long

  @Update
  suspend fun updateHealthRecord(record: HealthRecordEntity)

  @Query("DELETE FROM health_records WHERE id = :id")
  suspend fun deleteHealthRecordById(id: Long)

  @Query("DELETE FROM health_records")
  suspend fun deleteAllHealthRecords()
}

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions ORDER BY dateEpochDay DESC, createdAt DESC")
  fun observeAllTransactions(): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(tx: TransactionEntity): Long

  @Update
  suspend fun updateTransaction(tx: TransactionEntity)

  @Query("DELETE FROM transactions WHERE id = :id")
  suspend fun deleteTransactionById(id: Long)

  @Query("DELETE FROM transactions")
  suspend fun deleteAllTransactions()
}
