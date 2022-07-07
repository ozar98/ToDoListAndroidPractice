package com.example.todopractice_exam

import android.annotation.SuppressLint
import android.app.Application
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReminderPageViewModel(application: Application):AndroidViewModel(application){
    private val db = DB.getInstance(application)
    private val dao = db.getMyDao()
    private val today="06.07.2022"

    suspend fun getTodayReminders():List<Remainders>{
        return dao.getTodayRemainder(today())
    }
    suspend fun getHighPriorityListReminder(): List<Remainders> {
        return dao.getHighPriorityFirst()
    }

    suspend fun getChosenReminder(id: Int): Remainders {
        return dao.getChosenReminder(id)
    }
    suspend fun getAllReminders():List<Remainders>{
        return dao.getAllRemainders()
    }
    suspend fun updateReminder(reminder: Remainders){
        return dao.updateDB(reminder)
    }
    suspend fun deleteReminder(id: Int){
        return dao.deleteEntity(id)
    }
    suspend fun insertNewReminder(reminder:Remainders){
        return dao.insertNewRemainder(reminder)
    }
    suspend fun getHighPriority():List<Remainders>{
        return dao.getHighPriority("High")
    }
}

fun today(): Long {
    val today = Calendar.getInstance()

    return today.timeInMillis
}


class MainViewModel(application: Application):AndroidViewModel(application){
    private val db = DB.getInstance(application)
    private val dao = db.getMyDao()



    suspend fun getAllReminders():List<Remainders>{
        return dao.getAllRemainders()
    }



    suspend fun getTodayReminders():List<Remainders>{
        return dao.getTodayRemainder(today())
    }
    suspend fun getChosenReminder(id: Int): Remainders {
        return dao.getChosenReminder(id)
    }
    suspend fun countAllReminders():Int{
        return dao.countAll()
    }


    suspend fun countTodayReminders():Int{
        return dao.countToday(today())
    }
    suspend fun countHighPriorityReminders():Int{
        return dao.countHighPriorityReminder()
    }




    suspend fun updateReminder(reminder: Remainders){
        return dao.updateDB(reminder)
    }
    suspend fun deleteReminder(id: Int){
        return dao.deleteEntity(id)
    }
    suspend fun insertNewReminder(reminder:Remainders){
        return dao.insertNewRemainder(reminder)
    }



}