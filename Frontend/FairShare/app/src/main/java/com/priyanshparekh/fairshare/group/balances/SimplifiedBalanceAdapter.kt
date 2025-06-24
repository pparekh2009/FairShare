package com.priyanshparekh.fairshare.group.balances;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.BalanceInfo

class SimplifiedBalanceAdapter(private val balanceInfo: List<BalanceInfo>, private val usernames: Map<Long, String>) : RecyclerView.Adapter<SimplifiedBalanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimplifiedBalanceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_simplified_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimplifiedBalanceAdapter.ViewHolder, position: Int) {
        holder.bindData(
            usernames[balanceInfo[position].userId] ?: "",
            usernames[balanceInfo[position].otherUserId] ?: "",
            balanceInfo[position].amount,
        )
    }

    override fun getItemCount(): Int = balanceInfo.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvUsername = view.findViewById<TextView>(R.id.user_name)!!
        private val tvOtherUsername = view.findViewById<TextView>(R.id.other_user_name)!!
        private val tvAmount = view.findViewById<TextView>(R.id.amount)!!

        fun bindData(userName: String, otherUserName: String, amount: Double) {
            tvUsername.text = userName
            tvOtherUsername.text = otherUserName
            tvAmount.text = "$$amount"
        }
    }
}