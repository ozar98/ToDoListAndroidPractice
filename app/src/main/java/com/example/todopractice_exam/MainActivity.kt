package com.example.todopractice_exam

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE

import android.view.View.VISIBLE
import androidx.annotation.RequiresApi

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.todopractice_exam.databinding.ActivityMainBinding
import com.example.todopractice_exam.databinding.CreateReminderPageBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {
    private var _bindingBS: CreateReminderPageBinding? = null
    private val bindingBS get() = _bindingBS!!

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var notesActivity: BottomSheetDialog? = null

    private val reminderAdapter = ReminderAdapter()

    // ViewModel для данной активити
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        _bindingBS = CreateReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notesActivity = BottomSheetDialog(this)
        notesActivity?.setContentView(bindingBS.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.remindersRv.adapter = reminderAdapter
        getListReminder()


        reminderAdapter.onItemClick = {
            showReminder(it)
        }

        var counterSelected = 0
        reminderAdapter.onCheckBoxClick = { id, isChecked ->
            counterSelected += if (isChecked) 1 else -1

            binding.deleteButton.visibility = if (counterSelected == 1) VISIBLE else GONE

                binding.deleteButton.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteReminder(id)
                    val listRemainders = viewModel.getAllReminders()
                    withContext(Dispatchers.Main) {
                        reminderAdapter.submitList(listRemainders)
                        setTexts()
                    }
                }
            }
        }
        setButtonsClick()
        setTexts()


    }


    @SuppressLint("SetTextI18n")
    fun setButtonsClick() {
        binding.todayButton.setOnClickListener {
            startActivity(Intent(this, ReminderPage::class.java))
        }
        binding.noteButton.setOnClickListener {
            startActivity(Intent(this, ReminderAllPage::class.java))
        }
        binding.priorityButton.setOnClickListener {
            startActivity(Intent(this, ReminderHighPage::class.java))
        }
        binding.newReminder.setOnClickListener {
            bindingBS.submit.text = "Set up reminder"
            setupNewReminder()
        }
        bindingBS.chooseDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    bindingBS.dateEditText.text =
                        "${if (day < 10) "0$day" else day}.${if ((month + 1) < 10) "0${month + 1}" else month + 1}.$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        bindingBS.chooseTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->

                bindingBS.timeEditText.text =
                    "${if (hour < 10) "0$hour" else hour}:${if (minute < 10) "0$minute" else minute}"
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

    }


    private fun setTexts() {
        CoroutineScope(Dispatchers.IO).launch {
            val countToday = viewModel.countTodayReminders()
            val countAllRemainders = viewModel.countAllReminders()
            val countHighPriority = viewModel.countHighPriorityReminders()
            withContext(Dispatchers.Main) {
                binding.countToday.text = countToday.toString()
                binding.countAll.text = countAllRemainders.toString()
                binding.countPriority.text = countHighPriority.toString()
            }
        }
    }


    private fun getListReminder() {

        CoroutineScope(Dispatchers.IO).launch {

            val listOfReminders = viewModel.getAllReminders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
                setTexts()
            }

        }
    }

    @SuppressLint("SetTextI18n")
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
                val listOfReminders = viewModel.getAllReminders()
                withContext(Dispatchers.Main) {
                    reminderAdapter.submitList(listOfReminders)
                    notesActivity?.dismiss()
                }
            }
            setTexts()
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
            setTexts()
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
}