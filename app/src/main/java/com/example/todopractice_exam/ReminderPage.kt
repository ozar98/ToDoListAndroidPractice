package com.example.todopractice_exam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.todopractice_exam.databinding.ActivityMainBinding
import com.example.todopractice_exam.databinding.ActivityReminderPageBinding
import com.example.todopractice_exam.databinding.CreateReminderPageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ReminderPage : AppCompatActivity() {

    private val reminderAdapter=ReminderAdapter()

    private var _binding: ActivityReminderPageBinding? = null
    private val binding get() = _binding!!

    private var _bindingBS: CreateReminderPageBinding? = null
    private val bindingBS get() = _bindingBS!!

    private var notesActivity: BottomSheetDialog? = null

    private lateinit var viewModel: ReminderPageViewModel

    private lateinit var category:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityReminderPageBinding.inflate(layoutInflater)
        _bindingBS = CreateReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReminderPageViewModel::class.java]

        binding.remindersRv.adapter = reminderAdapter
        notesActivity = BottomSheetDialog(this)
        notesActivity?.setContentView(bindingBS.root)

        category= getReminderTitle()!!

        if (category != null) {
            getListReminder()
        }
        binding.goBackButton.setOnClickListener {
            finish()
        }
        var counterSelected = 0
        reminderAdapter.onCheckBoxClick = { id, isChecked ->
            counterSelected += if (isChecked) 1 else -1

            binding.deleteButton.visibility = if (counterSelected == 1) View.VISIBLE else View.GONE

            binding.deleteButton.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteReminder(id)
                    val listRemainders = viewModel.getAllReminders()
                    withContext(Dispatchers.Main) {
                        reminderAdapter.submitList(listRemainders)
                    }
                }
            }
        }
        reminderAdapter.onItemClick = {
            showReminder(it)
        }



    }
    private fun setupNewReminder() {
        bindingBS.nameEditText.setText("")
        bindingBS.descriptionEditText.setText("")
        bindingBS.dateEditText.text = ""
        bindingBS.timeEditText.text = ""
        bindingBS.locationEditText.setText("")
        bindingBS.priorityEditText.setText("")
        notesActivity?.show()
        bindingBS.submit.setOnClickListener {
            insertEntityDB()
            notesActivity?.dismiss()
        }

    }

    private fun insertEntityDB() {
        val reminder = setDataToReminder(0)
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.insertNewReminder(reminder)
            val listOfReminders = viewModel.getAllReminders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }
    private fun getListReminder() {
        CoroutineScope(Dispatchers.IO).launch {

            val listOfReminders=getList()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
                binding.title.text=category+" Reminders"
            }

        }
    }
    suspend fun getList():List<Remainders>{
        var listOfReminders= listOf<Remainders>()
        when (category) {
            "Today's " -> {
                listOfReminders= viewModel.getTodayReminders()
            }
            "All " -> {
                listOfReminders=viewModel.getAllReminders()
            }
            "High Priority " -> listOfReminders=viewModel.getHighPriority()
        }
        return listOfReminders
    }
    private fun getReminderTitle(): String? {
        val intent = Intent()
        val category = getIntent().getStringExtra("TITLE")
        return category

    }
    private fun setDataToReminder(id: Int): Remainders {
        val date = bindingBS.dateEditText.text.toString()
        val time = bindingBS.timeEditText.text.toString()

        val calendar = Calendar.getInstance()
        calendar.set(
            date.substring(date.length - 4).toInt(),
            date.substring(3, 5).toInt() - 1,
            date.substring(0, 2).toInt(),
            time.substring(0, 2).toInt(),
            time.substring(3).toInt()
        )

        return Remainders(
            id,
            bindingBS.nameEditText.text.toString(),
            bindingBS.descriptionEditText.text.toString(),
            calendar.timeInMillis,
            bindingBS.locationEditText.text.toString(),
            bindingBS.priorityEditText.text.toString())
    }
    fun showReminder(id: Int) {
        notesActivity?.show()
        CoroutineScope(Dispatchers.IO).launch {
            val chosenReminder = viewModel.getChosenReminder(id)
            withContext(Dispatchers.Main) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = chosenReminder.date

                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                bindingBS.nameEditText.setText(chosenReminder.name)
                bindingBS.descriptionEditText.setText(chosenReminder.description)
                bindingBS.dateEditText.text = "${if (day < 10) "0$day" else day}.${if ((month + 1) < 10) "0${month + 1}" else month + 1}.$year"
                bindingBS.timeEditText.text = "${if (hour < 10) "0$hour" else hour}:${if (minute < 10) "0$minute" else minute}"
                bindingBS.locationEditText.setText(chosenReminder.location)
                bindingBS.priorityEditText.setText(chosenReminder.priority)
            }
        }
        bindingBS.submit.text = "Edit reminder"
        bindingBS.submit.setOnClickListener {
            val reminder = setDataToReminder(id)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.updateReminder(reminder)
                val listOfReminders = getList()
                withContext(Dispatchers.Main) {
                    reminderAdapter.submitList(listOfReminders)
                    notesActivity?.dismiss()
                }
            }
        }
    }
}