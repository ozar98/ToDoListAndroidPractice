package com.example.todopractice_exam


import androidx.room.Entity
import java.sql.Date
import java.sql.Time


class Reminders(var name:String,
                var description: String,
                var date:String,
                var time: String,
                var location: String,
                var priority: String)