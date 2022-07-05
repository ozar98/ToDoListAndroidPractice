package com.example.todopractice_exam

import android.content.Context
import androidx.room.*
import java.sql.Date
import java.sql.Time


@Database(entities = [Remainders::class], version = 1)
abstract class DB: RoomDatabase() {
    abstract fun getMyDao(): MyDao

    companion object{
        @Volatile
        private var INSTANCE: DB?=null

        fun getInstance(context: Context):DB{
            if(INSTANCE!= null) return INSTANCE!!
            synchronized(this){
                INSTANCE= Room
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
    var name:String,
    var description: String,
    var date:String,//Date
    var time: String,
    var location: String,
    var priority: String
)

@Dao
interface MyDao{
    @Query("SELECT * FROM remainders")
    suspend fun getAllRemainders(): List<Remainders>

    @Query("SELECT * FROM remainders WHERE id like :chosenID")
    fun getChosenReminder(chosenID: Int):Remainders

    @Insert
    fun insertNewRemainder(reminder: Remainders)

    @Update
    fun updateDB(reminder: Remainders)

    @Query("DELETE FROM remainders WHERE id=:id")
    fun deleteEntity(id:Int)

    @Query("SELECT COUNT(id) FROM remainders where date=:today")
    fun countToday(today:String):Int

    @Query("SELECT COUNT(id) from remainders")
    fun countAll():Int

    @Query("SELECT * FROM Remainders where date=:today")
    fun getTodayRemainder(today: String):List<Remainders>

    @Query("SELECT * FROM Remainders ORDER BY CASE WHEN priority=:priority THEN 1 WHEN priority= 'Medium' THEN 2 ELSE 3 END ")
    suspend fun getHighPriorityFirst(priority:String="High"):List<Remainders>

    @Query("SELECT COUNT(id) from remainders where priority=:priority")
    fun countHighPriorityReminder(priority: String="High"):Int

}
