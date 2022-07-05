package com.example.todopractice_exam

import android.content.Intent
import android.icu.util.LocaleData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.example.todopractice_exam.databinding.ActivityMainBinding
import com.example.todopractice_exam.databinding.CreateReminderPageBinding

import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import java.time.LocalDate
import java.util.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private var _bindingBS:CreateReminderPageBinding?=null
    private val bindingBS get()=_bindingBS!!

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var notesActivity: BottomSheetDialog? = null

    private val reminderAdapter = ReminderAdapter()

    private lateinit var db: DB
    private lateinit var dao: MyDao

    // ViewModel для данной активити
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        _bindingBS=CreateReminderPageBinding.inflate(layoutInflater)
        db = DB.getInstance(this)
        dao = db.getMyDao()

        notesActivity = BottomSheetDialog(this)

        notesActivity?.setContentView(bindingBS.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContentView(binding.root)
        binding.remindersRv.adapter = reminderAdapter
        getListReminder()
        setupNotesActivity()

        setTexts()

        reminderAdapter.onItemClick = {
            showReminder(it)
        }
        reminderAdapter.onCheckBoxClick={

            binding.deleteButton.visibility=VISIBLE
            binding.deleteButton.setOnClickListener { _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.deleteReminder(it)
                    val listRemainders=viewModel.getAllReminders()
                    withContext(Dispatchers.Main){
                        reminderAdapter.submitList(listRemainders)
                    }
                }
            }
        }
        setButtonsClick()



    }
    fun setButtonsClick(){
        binding.todayButton.setOnClickListener {
            startActivity(Intent(this, ReminderPage::class.java))
        }
        binding.noteButton.setOnClickListener {
            startActivity(Intent(this, ReminderAllPage::class.java))
        }
        binding.priorityButton.setOnClickListener {
            startActivity(Intent(this, ReminderHighPage::class.java))
        }
    }

    fun setTexts(){
        CoroutineScope(Dispatchers.IO).launch {
            val countToday=viewModel.countTodayReminders()
            val countAllRemainders=viewModel.countAllReminders()
            withContext(Dispatchers.Main){
                binding.countToday.setText(countToday.toString())
                binding.countAll.setText(countAllRemainders.toString())
            }
        }
    }




    private fun getListReminder() {

        CoroutineScope(Dispatchers.IO).launch {

            val listOfReminders = viewModel.getAllReminders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }

    fun showReminder(id: Int) {
        notesActivity?.show()
        CoroutineScope(Dispatchers.IO).launch {
            val chosenReminder = viewModel.getChosenReminder(id)
            withContext(Dispatchers.Main) {
                bindingBS.nameEditText.setText(chosenReminder.name)
                bindingBS.descriptionEditText.setText(chosenReminder.description)
                bindingBS.dateEditText.setText(chosenReminder.date)
                bindingBS.timeEditText.setText(chosenReminder.time)
                bindingBS.locationEditText.setText(chosenReminder.location)
                bindingBS.priorityEditText.setText(chosenReminder.priority)
            }
        }
        bindingBS.submit.setText("Edit reminder")
        bindingBS.submit.setOnClickListener {
            notesActivity?.show()
            val reminder=setDataToReminder()
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.updateReminder(reminder)
                val listOfReminders = viewModel.getAllReminders()
                withContext(Dispatchers.Main) {
                    reminderAdapter.submitList(listOfReminders)
                }
            }
            notesActivity?.dismiss()
            setTexts()
        }
    }


    fun setupNotesActivity() {

        binding.newReminder.setOnClickListener {
            bindingBS.nameEditText.setText("")
            bindingBS.descriptionEditText.setText("")
            bindingBS.dateEditText.setText("")
            bindingBS.timeEditText.setText("")
            bindingBS.locationEditText.setText("")
            bindingBS.priorityEditText.setText("")
            notesActivity?.show()
            bindingBS.submit.setOnClickListener {
                insertEntityDB()
                setTexts()
            }
        }
    }

    fun insertEntityDB() {
        val reminder=setDataToReminder()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.inserNewReminder(reminder)
            val listOfReminders = viewModel.getAllReminders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }
    fun setDataToReminder(): Remainders{
        val name = bindingBS.nameEditText.text.toString()
        val description = bindingBS.descriptionEditText.text.toString()
        val date = bindingBS.dateEditText.text.toString()
        val time = bindingBS.timeEditText.text.toString()
        val location = bindingBS.locationEditText.text.toString()
        val priority = bindingBS.priorityEditText.text.toString()
        return Remainders(0,name, description, date, time, location, priority)
    }
}