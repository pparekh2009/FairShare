package com.priyanshparekh.fairshare.group.expenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.User

class SplitAmongAdapter(private val users: List<User>) : RecyclerView.Adapter<SplitAmongAdapter.ViewHolder>() {

    private val selectedUsers = mutableSetOf<User>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkboxUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_selection_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.checkbox.text = user.name
        holder.checkbox.isChecked = selectedUsers.contains(user)

        holder.checkbox.setOnCheckedChangeListener(null) // Prevent unwanted triggers during recycling
        holder.checkbox.isChecked = selectedUsers.contains(user)

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedUsers.add(user)
            } else {
                selectedUsers.remove(user)
            }
        }
    }

    fun getSelectedUsers(): List<User> = selectedUsers.toList()

    fun unselectAll() {
        selectedUsers.removeAll(selectedUsers)
    }
}