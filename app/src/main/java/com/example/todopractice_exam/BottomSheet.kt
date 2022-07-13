package com.example.todopractice_exam

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.todopractice_exam.databinding.CreateReminderPageBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BottomSheet(context: Context) : BottomSheetDialog(context) {
    private var _bindingBS: CreateReminderPageBinding? = null
    private val bindingBS get() = _bindingBS!!
    private lateinit var priority:List<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bindingBS = CreateReminderPageBinding.inflate(layoutInflater)
        setContentView(bindingBS.root)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _bindingBS = null
    }




}