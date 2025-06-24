package com.priyanshparekh.fairshare.group.expenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.Expense

class ExpenseAdapter(private val expenseList: List<Expense>, private val usernames: Map<Long, String>) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_expense_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.expenseName.text = expenseList[position].name
        holder.amount.text = "$${expenseList[position].amount}"
        holder.payerName.text = usernames[expenseList[position].paidBy]
    }

    override fun getItemCount(): Int = expenseList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val expenseName = view.findViewById<TextView>(R.id.expense_name_label)!!
        val amount = view.findViewById<TextView>(R.id.amount_label)!!
        val payerName = view.findViewById<TextView>(R.id.payer_name_label)!!
    }
}