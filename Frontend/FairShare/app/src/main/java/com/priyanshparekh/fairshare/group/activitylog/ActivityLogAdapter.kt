package com.priyanshparekh.fairshare.group.activitylog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.ActivityLog

class ActivityLogAdapter(private val activityLogs: List<ActivityLog>) : RecyclerView.Adapter<ActivityLogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivityLogAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_activity_log_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityLogAdapter.ViewHolder, position: Int) {
        holder.activityLog.text = activityLogs[position].log
    }

    override fun getItemCount(): Int = activityLogs.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val activityLog = view.findViewById<TextView>(R.id.activity_log_item)!!
    }
}