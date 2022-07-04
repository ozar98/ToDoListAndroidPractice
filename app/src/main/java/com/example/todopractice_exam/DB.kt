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
    @PrimaryKey(autoGenerate = true) val id:Int,
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
    fun getAllRemainders(): List<Remainders>

    @Query("SELECT * FROM remainders WHERE id like :chosenID")
    fun getChosenReminder(chosenID: Int):List<Remainders>

    @Insert
    fun insertNewRemainder(reminder: Remainders)

    @Update
    fun updateDB(reminder: Remainders)


}
