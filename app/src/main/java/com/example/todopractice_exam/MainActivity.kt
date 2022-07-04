package com.example.todopractice_exam

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
import com.example.todopractice_exam.databinding.ActivityMainBinding
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

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var notesActivity: BottomSheetDialog? = null

    private val reminderAdapter = ReminderAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.remindersRv.adapter = reminderAdapter

        setupListeners()
        getListReminder()
        setupNotesActivity()

        reminderAdapter.onItemClick = {
            showReminder(it)
        }
        reminderAdapter.onCheckBoxClick={
            binding.deleteButton.visibility=VISIBLE
            binding.deleteButton.setOnClickListener { _ ->
                val db = DB.getInstance(this)
                val dao = db.getMyDao()
                CoroutineScope(Dispatchers.IO).launch {
                    dao.deleteEntity(it)
                    val list=dao.getAllRemainders()
                    withContext(Dispatchers.Main){
                        reminderAdapter.submitList(list)
                    }
                }
            }
        }



    }


    fun setupListeners() {
        getListReminder()

    }

    private fun getListReminder() {
        val db = DB.getInstance(this)
        val dao = db.getMyDao()
        CoroutineScope(Dispatchers.IO).launch {

            val listOfReminders = dao.getAllRemainders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }

    fun showReminder(id: Int) {
        Log.d("TAG_test", "showReminder: $id")
        val db = DB.getInstance(this)
        val dao = db.getMyDao()
        notesActivity = BottomSheetDialog(this)
        notesActivity?.setContentView(R.layout.create_reminder_page)
        notesActivity?.show()
        CoroutineScope(Dispatchers.IO).launch {
            val chosenReminder = dao.getChosenReminder(id)
            withContext(Dispatchers.Main) {
                notesActivity?.findViewById<EditText>(R.id.name_edit_text)
                    ?.setText(chosenReminder[0].name)
                notesActivity?.findViewById<EditText>(R.id.description_edit_text)
                    ?.setText(chosenReminder[0].description)
                notesActivity?.findViewById<EditText>(R.id.date_edit_text)
                    ?.setText(chosenReminder[0].date)
                notesActivity?.findViewById<EditText>(R.id.time_edit_text)
                    ?.setText(chosenReminder[0].time)
                notesActivity?.findViewById<EditText>(R.id.location_edit_text)
                    ?.setText(chosenReminder[0].location)
                notesActivity?.findViewById<EditText>(R.id.priority_edit_text)
                    ?.setText(chosenReminder[0].priority)
            }
        }
        notesActivity?.findViewById<TextView>(R.id.submit)?.setOnClickListener {
            notesActivity?.show()
            notesActivity?.findViewById<TextView>(R.id.submit)?.text = "Edit remainder"
            val name = notesActivity?.findViewById<EditText>(R.id.name_edit_text)?.text.toString()
            val description = notesActivity?.findViewById<EditText>(R.id.description_edit_text)?.text.toString()
            val date = notesActivity?.findViewById<EditText>(R.id.date_edit_text)?.text.toString()
            val time = notesActivity?.findViewById<EditText>(R.id.time_edit_text)?.text.toString()
            val location = notesActivity?.findViewById<EditText>(R.id.location_edit_text)?.text.toString()
            val priority = notesActivity?.findViewById<EditText>(R.id.priority_edit_text)?.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                dao.updateDB(Remainders(id, name, description, date, time, location, priority))
                val listOfReminders = dao.getAllRemainders()
                withContext(Dispatchers.Main) {
                    reminderAdapter.submitList(listOfReminders)
                }
            }

            notesActivity?.dismiss()
        }
    }


    fun setupNotesActivity() {
        val db = DB.getInstance(this)
        val dao = db.getMyDao()
        notesActivity = BottomSheetDialog(this)
        notesActivity?.setContentView(R.layout.create_reminder_page)
        binding.noteButton.setOnClickListener {
            notesActivity?.show()
            notesActivity?.findViewById<TextView>(R.id.submit)?.setOnClickListener {
                insertEntityDB(dao)
            }
        }
    }

    fun insertEntityDB(dao: MyDao) {
        val name = notesActivity?.findViewById<EditText>(R.id.name_edit_text)?.text.toString()
        val description =
            notesActivity?.findViewById<EditText>(R.id.description_edit_text)?.text.toString()
        val date = notesActivity?.findViewById<EditText>(R.id.date_edit_text)?.text.toString()
        val time = notesActivity?.findViewById<EditText>(R.id.time_edit_text)?.text.toString()
        val location =
            notesActivity?.findViewById<EditText>(R.id.location_edit_text)?.text.toString()
        val priority =
            notesActivity?.findViewById<EditText>(R.id.priority_edit_text)?.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            dao.insertNewRemainder(Remainders(0, name, description, date, time, location, priority))
            val listOfReminders = dao.getAllRemainders()
            withContext(Dispatchers.Main) {
                reminderAdapter.submitList(listOfReminders)
            }

        }
    }
}