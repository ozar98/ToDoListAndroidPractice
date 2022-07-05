package com.example.todopractice_exam

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ReminderHighPageViewModel(application: Application): AndroidViewModel(application) {
    private val db = DB.getInstance(application)
    private val dao = db.getMyDao()

    suspend fun getHighPriorityListReminder(): List<Remainders> {
        return dao.getHighPriorityFirst()
    }
}

class MainViewModel(application: Application):AndroidViewModel(application){
    private val db = DB.getInstance(application)
    private val dao = db.getMyDao()

    private val today="05.07.2022"
    suspend fun getAllReminders():List<Remainders>{
        return dao.getAllRemainders()
    }
    suspend fun countAllReminders():Int{
        return dao.countAll()
    }
    suspend fun getTodayReminders():List<Remainders>{
        return dao.getTodayRemainder(today)
    }
    suspend fun countTodayReminders():Int{
        return dao.countToday(today)
    }
    suspend fun countHighPriorityReminders():Int{
        return dao.countHighPriorityReminder()
    }
    suspend fun inserNewReminder(reminder:Remainders){
        return dao.insertNewRemainder(reminder)
    }
    suspend fun deleteReminder(id: Int){
        return dao.deleteEntity(id)
    }

    suspend fun getChosenReminder(id: Int):Remainders{
        val reminder=dao.getChosenReminder(id)
        return reminder
    }
    suspend fun updateReminder(reminder: Remainders){
        return dao.updateDB(reminder)
    }


}