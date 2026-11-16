package com.example.data.local

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
  entities = [
    CattleEntity::class,
    MilkRecordEntity::class,
    FeedItemEntity::class,
    FeedLogEntity::class,
    HealthRecordEntity::class,
    TransactionEntity::class,
  ],
  version = 2,
  exportSchema = false,
)
abstract class SmartDairyDatabase : RoomDatabase() {
  abstract fun cattleDao(): CattleDao
  abstract fun milkDao(): MilkDao
  abstract fun feedDao(): FeedDao
  abstract fun healthDao(): HealthDao
  abstract fun transactionDao(): TransactionDao

  companion object {
    @Volatile
    private var INSTANCE: SmartDairyDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE cattle ADD COLUMN dateOfBirthEpochDay INTEGER")
      }
    }

    fun getInstance(context: Context): SmartDairyDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          SmartDairyDatabase::class.java,
          "smart_dairy_farm.db"
        )
          .addMigrations(MIGRATION_1_2)
          .build()
          .also { INSTANCE = it }
      }
    }

    @VisibleForTesting
    internal fun resetForTesting() {
      synchronized(this) {
        INSTANCE?.close()
        INSTANCE = null
      }
    }
  }
}
