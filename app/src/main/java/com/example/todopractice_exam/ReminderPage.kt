package com.example.todopractice_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.todopractice_exam.databinding.ActivityMainBinding
import com.example.todopractice_exam.databinding.ActivityReminderPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReminderPage : AppCompatActivity() {

    private val reminderAdapter=ReminderAdapter()

    private var _binding: ActivityReminderPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.remindersRv.adapter = reminderAdapter
        getListReminder()
    }
    private fun getListReminder() {
        val db = DB.getInstance(this)
        val dao = db.getMyDao()
        CoroutineScope(Dispatchers.IO).launch {

            val listOfReminders = dao.getTodayRemainder("05.07.2022")
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }
}