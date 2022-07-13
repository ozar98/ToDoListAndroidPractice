package com.example.todopractice_exam

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
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

class ReminderPageActivity : AppCompatActivity() {

    private val reminderAdapter=ReminderAdapter()

    private var _binding: ActivityReminderPageBinding? = null
    private val binding get() = _binding!!

    private var _bindingBS: CreateReminderPageBinding? = null
    private val bindingBS get() = _bindingBS!!

    private var notesActivity: BottomSheetDialog? = null

    private lateinit var viewModel: ReminderPageViewModel

    private lateinit var category:String

    private lateinit var priority:List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding=ActivityReminderPageBinding.inflate(layoutInflater)
        _bindingBS = CreateReminderPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReminderPageViewModel::class.java]

        binding.remindersRv.adapter = reminderAdapter
        notesActivity = BottomSheetDialog(this)
        notesActivity?.setContentView(bindingBS.root)

        setPriority()


        binding.goBackButton.setOnClickListener {
            finish()
        }
        adapterClickS()
        chooseDateTime()
        observeLiveData()
        getCategory()

    }

    private fun getCategory() {
        category= getReminderTitle()!!

        if (category != null) {
            getListReminder()
        }
    }

    private fun adapterClickS() {
        var counterSelected = 0
        reminderAdapter.onCheckBoxClick = { id, isChecked ->
            counterSelected += if (isChecked) 1 else -1

            binding.deleteButton.visibility = if (counterSelected == 1) View.VISIBLE else View.GONE


        }
        reminderAdapter.onItemClick = {
            showReminder(it)
        }
    }

    private fun setPriority() {
        priority=resources.getStringArray(R.array.Priorities).toList()


        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, priority
        )
        bindingBS.priorityEditText.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.allRemindersLB.observe(this) { reminders ->
            reminderAdapter.submitList(reminders)
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
    private suspend fun getList():List<Remainders>{
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
            priority[bindingBS.priorityEditText.selectedItemPosition])
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
                bindingBS.priorityEditText.setSelection(priority.indexOf(chosenReminder.priority))
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
    private fun chooseDateTime(){
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

    companion object {
        private const val TITLE = "TITLE"

        fun newIntent(context: Context, title: String): Intent {
            val intent = Intent(context, ReminderPageActivity::class.java)
            intent.putExtra(TITLE, title)
            return intent
        }
    }

}