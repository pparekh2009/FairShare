package com.priyanshparekh.fairshare.group.balances

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.BalanceInfoItem
import com.priyanshparekh.fairshare.utils.OnRvItemClickListener
import java.math.RoundingMode

class BalancesAdapter(private val balancesList: List<BalanceInfoItem>, private val onRvItemClickListener: OnRvItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        when (viewType) {
            ItemViewType.VIEW_TYPE_BUTTON.viewType -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_balances_button, parent, false)
                return ButtonViewHolder(view, onRvItemClickListener)
            }
            ItemViewType.VIEW_TYPE_HEADER.viewType -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_balances_header, parent, false)
                return HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_balances_item, parent, false)
                return ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ItemViewType.VIEW_TYPE_HEADER.viewType -> {
                (holder as HeaderViewHolder).headerText.text = balancesList[position].headerText
            }

            ItemViewType.VIEW_TYPE_BUTTON.viewType -> {
                (holder as ButtonViewHolder).button.text = balancesList[position].buttonText
            }

            ItemViewType.VIEW_TYPE_ITEM.viewType -> {
                (holder as ItemViewHolder).apply {
                    name.text = balancesList[position].username
                    amount.text = "$${balancesList[position].amount!!.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()}"
                }
            }
        }
    }

    override fun getItemCount(): Int = balancesList.size

    override fun getItemViewType(position: Int): Int {
        return balancesList[position].viewType.viewType
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerText = view.findViewById<TextView>(R.id.balances_header)!!
    }

    inner class ButtonViewHolder(view: View, private val onRvItemClickListener: OnRvItemClickListener) : RecyclerView.ViewHolder(view), OnClickListener {
        val button = view.findViewById<MaterialButton>(R.id.balances_btn)!!

        init {
            button.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onRvItemClickListener.onItemClick(absoluteAdapterPosition)
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.bal_name)!!
        val amount = view.findViewById<TextView>(R.id.bal_amount)!!
    }

    enum class ItemViewType(val viewType: Int) {
        VIEW_TYPE_BUTTON(0),
        VIEW_TYPE_HEADER(1),
        VIEW_TYPE_ITEM(2)
    }
}