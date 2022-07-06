package com.example.todopractice_exam

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var viewModel: ReminderPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReminderPageViewModel::class.java]

        binding.remindersRv.adapter = reminderAdapter
        getListReminder()
    }
    private fun getListReminder() {
        CoroutineScope(Dispatchers.IO).launch {

            val listOfReminders = viewModel.getTodayReminders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }
}