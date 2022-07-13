package com.example.todopractice_exam

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Database(entities = [Remainders::class], version = 1)
abstract class DB : RoomDatabase() {
    abstract fun getMyDao(): MyDao

    companion object {
        @Volatile
        private var INSTANCE: DB? = null

        fun getInstance(context: Context): DB {
            if (INSTANCE != null) return INSTANCE!!
            synchronized(this) {
                INSTANCE = Room
                    .databaseBuilder(context, DB::class.java, "reminder_db")
                    .build()
                return INSTANCE!!
            }

        }
    }
}

@Entity
data class Remainders(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var name: String,
    var description: String,
    var date: Long,//Date
    var location: String,
    var priority: String
)

@Dao
interface MyDao {
    @Query("SELECT * FROM remainders")
    suspend fun getAllRemainders(): List<Remainders>

    @Query("SELECT * FROM remainders WHERE id like :chosenID")
    fun getChosenReminder(chosenID: Int): Remainders

    @Query("SELECT * FROM Remainders where date>=:today AND date <= (:today+(3600*24000))")
    fun getTodayRemainder(today: Long): List<Remainders>

    @Query("SELECT * FROM Remainders where priority=:priority")
    fun getHighPriority(priority: String="High"):List<Remainders>

    @Query("SELECT * FROM Remainders ORDER BY CASE WHEN priority=:priority THEN 1 WHEN priority= 'Medium' THEN 2 ELSE 3 END ")
    suspend fun getHighPriorityFirst(priority: String = "High"): List<Remainders>


    @Insert
    fun insertNewRemainder(reminder: Remainders)

    @Update
    fun updateDB(reminder: Remainders)

    @Query("DELETE FROM remainders WHERE id=:id")
    fun deleteEntity(id: Int)

    @Query("SELECT COUNT(id) FROM remainders where date>=:today AND date <= (:today+(3600*24000))")
    fun countToday(today: Long): LiveData<Int>

    @Query("SELECT COUNT(id) from remainders")
    fun countAll(): LiveData<Int>


    @Query("SELECT COUNT(id) from remainders where priority=:priority")
    fun countHighPriorityReminder(priority: String = "High"): LiveData<Int>


    //Queries with LiveData
    @Query("SELECT * FROM remainders")
    fun getRemindersLD(): LiveData<List<Remainders>>

    @Query("SELECT * FROM remainders WHERE id like :chosenID")
    fun getChosenRemindersLD(chosenID: Int): LiveData<List<Remainders>>

    @Query("SELECT * FROM Remainders where date>=:today AND date <= (:today+(3600*24000))")
    fun getTodayRemaindersLD(today: Long): LiveData<List<Remainders>>

    @Query("SELECT * FROM Remainders where priority=:priority")
    fun getHighPriorityRemindersLD(priority: String="High"):LiveData<List<Remainders>>




}
