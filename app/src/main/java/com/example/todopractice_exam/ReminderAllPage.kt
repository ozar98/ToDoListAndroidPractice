package com.example.todopractice_exam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todopractice_exam.databinding.ActivityReminderPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReminderAllPage : AppCompatActivity() {

    private val reminderAdapter=ReminderAdapter()

    private var _binding: ActivityReminderPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel:ReminderAllPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this)[ReminderAllPageViewModel::class.java]

        binding.remindersRv.adapter = reminderAdapter
        getListReminder()
    }
    private fun getListReminder() {
        CoroutineScope(Dispatchers.IO).launch {

            val listOfReminders = viewModel.getAllReminders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }
}