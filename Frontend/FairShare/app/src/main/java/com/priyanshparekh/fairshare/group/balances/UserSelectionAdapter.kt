package com.priyanshparekh.fairshare.group.balances

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.BalanceInfoItem

class UserSelectionAdapter(private val users: List<BalanceInfoItem>) : RecyclerView.Adapter<UserSelectionAdapter.ViewHolder>() {

    private val selectedUsers = mutableSetOf<BalanceInfoItem>()

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
        val balanceInfoItem = users[position]
        holder.checkbox.text = balanceInfoItem.username
        holder.checkbox.isChecked = selectedUsers.contains(balanceInfoItem)

        holder.checkbox.setOnCheckedChangeListener(null) // Prevent unwanted triggers during recycling
        holder.checkbox.isChecked = selectedUsers.contains(balanceInfoItem)

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedUsers.add(balanceInfoItem)
            } else {
                selectedUsers.remove(balanceInfoItem)
            }
        }
    }

    fun getSelectedUsers(): List<BalanceInfoItem> = selectedUsers.toList()
}