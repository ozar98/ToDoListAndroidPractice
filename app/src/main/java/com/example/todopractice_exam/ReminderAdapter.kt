package com.example.todopractice_exam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    var listReminder: List<Remainders> = emptyList()

    fun submitList(newList: List<Remainders>) {
        listReminder = newList
        notifyDataSetChanged()
    }

    var onItemClick: ((Int) -> Unit)? = null
    var onCheckBoxClick: ((Int) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.rv_reminder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.name.text = listReminder[position].name
        holder.date.text = listReminder[position].date.toString()
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
            view.findViewById<CheckBox>(R.id.radio_button)
                .setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked)
                        onCheckBoxClick?.invoke(listReminder[adapterPosition].id)
                }

        }
    }
}