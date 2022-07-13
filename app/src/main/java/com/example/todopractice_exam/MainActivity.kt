package com.example.todopractice_exam

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE

import android.view.View.VISIBLE
import android.widget.ArrayAdapter

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.todopractice_exam.databinding.ActivityMainBinding
import com.example.todopractice_exam.databinding.CreateReminderPageBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private var _bindingBS: CreateReminderPageBinding? = null
    private val bindingBS get() = _bindingBS!!

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var notesActivity: BottomSheetDialog? = null

    private val reminderAdapter = ReminderAdapter()

    // ViewModel для данной активити
    private lateinit var viewModel: MainViewModel
    private lateinit var priority:List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        _bindingBS = CreateReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notesActivity = BottomSheetDialog(this)
        notesActivity?.setContentView(bindingBS.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.remindersRv.adapter = reminderAdapter
        observeLiveData()

        setupSpinner()

        adapterClicks()
        setButtonsClick()
    }

    private fun observeLiveData() {
        viewModel.remindersLV.observe(this) { reminders ->
            reminderAdapter.submitList(reminders)
        }
        viewModel.countAllReminders.observe(this){
            binding.countAll.text=it.toString()
        }
        viewModel.countHighPriorityReminders.observe(this){
            binding.countPriority.text=it.toString()
        }
        viewModel.countTodayReminders.observe(this){
            binding.countToday.text=it.toString()
        }
    }
    private fun adapterClicks(){
        var counterSelected=0
        reminderAdapter.onCheckBoxClick = { id, isChecked ->
            counterSelected += if (isChecked) 1 else -1

            binding.deleteButton.visibility = if (counterSelected == 1) VISIBLE else GONE

            binding.deleteButton.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteReminder(id)

                }
            }
        }

        reminderAdapter.onItemClick = {
            showReminderAndUpdate(it)
        }
    }


    @SuppressLint("SetTextI18n")
    fun setButtonsClick() {
        val listReminderCategory = listOf("Today's ", "All ", "High Priority ")
        binding.todayLayout.setOnClickListener {
            launchReminderPageActivity(listReminderCategory[0])
        }
        binding.allLayout.setOnClickListener {
            launchReminderPageActivity(listReminderCategory[1])
        }
        binding.priorityLayout.setOnClickListener {
            launchReminderPageActivity(listReminderCategory[2])
        }
        binding.todayButton.setOnClickListener {
            launchReminderPageActivity(listReminderCategory[0])
        }
        binding.noteButton.setOnClickListener {
            launchReminderPageActivity(listReminderCategory[1])
        }
        binding.priorityButton.setOnClickListener {
            launchReminderPageActivity(listReminderCategory[2])
        }


        binding.newReminder.setOnClickListener {
            bindingBS.submit.text = "Set up reminder"
            setupNewReminder()
        }
        bindingBS.chooseDate.setOnClickListener {
            chooseDate()
        }
        bindingBS.chooseTime.setOnClickListener {
            chooseTime()
        }
        bindingBS.goBackButton.setOnClickListener {
            notesActivity?.dismiss()
        }

    }

    private fun chooseDate(){
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

    private fun chooseTime(){
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->

            bindingBS.timeEditText.text =
                "${if (hour < 10) "0$hour" else hour}:${if (minute < 10) "0$minute" else minute}"
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun launchReminderPageActivity(title: String) {
        ReminderPageActivity.newIntent(this, title).also {
            startActivity(it)
        }
    }




    @SuppressLint("SetTextI18n")
    fun showReminderAndUpdate(id: Int) {
        notesActivity?.show()
        getChosenReminder(id)
        bindingBS.submit.text = "Edit reminder"
        updateReminder(id)
    }
    private fun getChosenReminder(id: Int){
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
                bindingBS.dateEditText.text =
                    "${if (day < 10) "0$day" else day}.${if ((month + 1) < 10) "0${month + 1}" else month + 1}.$year"
                bindingBS.timeEditText.text =
                    "${if (hour < 10) "0$hour" else hour}:${if (minute < 10) "0$minute" else minute}"
                bindingBS.locationEditText.setText(chosenReminder.location)
                bindingBS.priorityEditText.setSelection(priority.indexOf(chosenReminder.priority))
            }
        }
    }
    private fun updateReminder(id: Int){
        bindingBS.submit.setOnClickListener {
            val reminder = setDataToReminder(id)
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.updateReminder(reminder)
                withContext(Dispatchers.Main) {
                    notesActivity?.dismiss()
                }
            }
        }
    }



    private fun setupNewReminder() {
        bindingBS.nameEditText.setText("")
        bindingBS.descriptionEditText.setText("")
        bindingBS.dateEditText.text = ""
        bindingBS.timeEditText.text = ""
        bindingBS.locationEditText.setText("")

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
            priority[bindingBS.priorityEditText.selectedItemPosition]
        )
    }
    private fun setupSpinner(){
        priority=resources.getStringArray(R.array.Priorities).toList()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, priority
        )
        bindingBS.priorityEditText.adapter = adapter
    }

}