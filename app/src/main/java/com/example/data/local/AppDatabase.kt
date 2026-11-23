package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Schedules
    @Query("SELECT * FROM schedules ORDER BY isActive DESC, createdAt DESC")
    fun getAllSchedules(): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules ORDER BY isActive DESC, createdAt DESC")
    suspend fun getAllSchedulesOnce(): List<ScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Int)

    // ChatStudy AI History
    @Query("SELECT * FROM chat_history ORDER BY timestamp DESC")
    fun getAllChatHistory(): Flow<List<ChatHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatHistory(entry: ChatHistoryEntity)

    @Query("DELETE FROM chat_history WHERE id = :id")
    suspend fun deleteChatHistoryById(id: Int)

    @Query("DELETE FROM chat_history")
    suspend fun clearAllChatHistory()

    // Exam Results & Marks Ledger
    @Query("SELECT * FROM exam_results ORDER BY timestamp DESC")
    fun getAllExamResults(): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultEntity): Long

    @Update
    suspend fun updateExamResult(result: ExamResultEntity)

    @Query("DELETE FROM exam_results WHERE id = :id")
    suspend fun deleteExamResultById(id: Int)

    // Coin Vault & Social Media Screen-Time State
    @Query("SELECT * FROM coin_social_state WHERE id = 1")
    fun getCoinSocialState(): Flow<CoinSocialStateEntity?>

    @Query("SELECT * FROM coin_social_state WHERE id = 1")
    suspend fun getCoinSocialStateOnce(): CoinSocialStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCoinSocialState(state: CoinSocialStateEntity)

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfileEntity)
}

@Database(
    entities = [
        ScheduleEntity::class,
        ChatHistoryEntity::class,
        ExamResultEntity::class,
        CoinSocialStateEntity::class,
        UserProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ar_creator_counselling_timetable.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
