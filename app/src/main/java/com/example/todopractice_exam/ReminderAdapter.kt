package com.example.todopractice_exam

import android.graphics.Paint
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    var listReminder: List<Remainders> = emptyList()

    fun submitList(newList: List<Remainders>) {
        listReminder = newList
        notifyDataSetChanged()
    }

    var onItemClick: ((Int) -> Unit)? = null
    var onCheckBoxClick: ((Int,Boolean) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_reminder, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.name.text = listReminder[position].name
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = listReminder[position].date
        holder.date.text = "${calendar.get(Calendar.DAY_OF_MONTH)}.${calendar.get(Calendar.MONTH)}.${calendar.get(Calendar.YEAR)}"

        @RequiresApi(Build.VERSION_CODES.N)
        if (listReminder[position].date < today()) {
            holder.name.text = Html.fromHtml("<strike>${holder.name.text}</strike>", Html.FROM_HTML_MODE_LEGACY)
        }

    }

    override fun getItemCount(): Int {
        return listReminder.size
    }

    inner class ReminderViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.reminder_name)
        val date: TextView = view.findViewById(R.id.reminder_date)



        init {
            view.findViewById<ConstraintLayout>(R.id.layout_rv).setOnClickListener {
                onItemClick?.invoke(listReminder[adapterPosition].id)
            }
            view.findViewById<ImageButton>(R.id.info).setOnClickListener {
                onItemClick?.invoke(listReminder[adapterPosition].id)
            }
            view.findViewById<CheckBox>(R.id.radio_button)
                .setOnCheckedChangeListener { _, isChecked ->
                    onCheckBoxClick?.invoke(listReminder[adapterPosition].id,isChecked)

                }

        }
    }
}