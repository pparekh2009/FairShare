package com.priyanshparekh.fairshare.home

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.Group
import com.priyanshparekh.fairshare.utils.OnRvItemClickListener

class GroupListAdapter(private val groupList: List<Group>, private val onRvItemClickListener: OnRvItemClickListener): RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_list_item, parent, false)
        return ViewHolder(view, onRvItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = groupList[position].name
    }

    override fun getItemCount(): Int = groupList.size

    class ViewHolder(view: View, private val onRvItemClickListener: OnRvItemClickListener) : RecyclerView.ViewHolder(view), OnClickListener {
        val textView = view.findViewById<TextView>(R.id.group_name)

        init {
            textView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onRvItemClickListener.onItemClick(adapterPosition)
        }
    }
}