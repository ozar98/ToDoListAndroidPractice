package com.example.todopractice_exam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.todopractice_exam.databinding.ActivityReminderPageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReminderHighPage : AppCompatActivity() {

    private val reminderAdapter = ReminderAdapter()

    private var _binding: ActivityReminderPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReminderHighPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ReminderHighPageViewModel::class.java]
        binding.remindersRv.adapter = reminderAdapter

        CoroutineScope(Dispatchers.IO).launch {
            val listOfRemainders = viewModel.getHighPriorityListReminder()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfRemainders)
            }
        }
    }
}