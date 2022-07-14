package com.example.todopractice_exam

import android.annotation.SuppressLint
import android.app.Application
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReminderPageViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DB.getInstance(application)
    private val dao = db.getMyDao()

    val todayRemindersLB: LiveData<List<Remainders>> = dao.getTodayRemaindersLD(today())
    val highPRemindersLB: LiveData<List<Remainders>> = dao.getHighPriorityRemindersLD()
    val allRemindersLB: LiveData<List<Remainders>> = dao.getRemindersLD()




    suspend fun getAllReminders(): List<Remainders> {
        return dao.getAllRemainders()
    }

    suspend fun getTodayReminders(): List<Remainders> {
        return dao.getTodayRemainder(today())
    }

    suspend fun getChosenReminder(id: Int): Remainders {
        return dao.getChosenReminder(id)
    }

    suspend fun updateReminder(reminder: Remainders) {
        return dao.updateDB(reminder)
    }

    suspend fun deleteReminder(id: Int) {
        return dao.deleteEntity(id)
    }

    suspend fun insertNewReminder(reminder: Remainders) {
        return dao.insertNewRemainder(reminder)
    }

    suspend fun getHighPriority(): List<Remainders> {
        return dao.getHighPriority("High")
    }
}

fun today(): Long {
    return Calendar.getInstance().timeInMillis - (Calendar.getInstance().timeInMillis % (3600 * 24000))
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DB.getInstance(application)
    private val dao = db.getMyDao()

    val remindersLV: LiveData<List<Remainders>> = dao.getRemindersLD()

    val countTodayReminders: LiveData<Int> = dao.countToday(today())
    val countHighPriorityReminders: LiveData<Int> = dao.countHighPriorityReminder()
    val countAllReminders: LiveData<Int> = dao.countAll()

    fun getRequestedReminder(string: String): LiveData<List<Remainders>>{
        return dao.getFoundRemainder(string)
    }

    suspend fun getAllReminders(): List<Remainders> {
        return dao.getAllRemainders()
    }

    suspend fun getTodayReminders(): List<Remainders> {
        return dao.getTodayRemainder(today())
    }

    suspend fun getChosenReminder(id: Int): Remainders {
        return dao.getChosenReminder(id)
    }

    suspend fun updateReminder(reminder: Remainders) {
        return dao.updateDB(reminder)
    }

    suspend fun deleteReminder(id: Int) {
        return dao.deleteEntity(id)
    }

    suspend fun insertNewReminder(reminder: Remainders) {
        return dao.insertNewRemainder(reminder)
    }
}