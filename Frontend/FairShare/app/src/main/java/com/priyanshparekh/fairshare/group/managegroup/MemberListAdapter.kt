package com.priyanshparekh.fairshare.group.managegroup;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.User

class MemberListAdapter(private val memberList: List<User>) : RecyclerView.Adapter<MemberListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_member_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberListAdapter.ViewHolder, position: Int) {
        holder.memberName.text = memberList[position].name
    }

    override fun getItemCount(): Int = memberList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val memberName = view.findViewById<TextView>(R.id.member_item)!!

    }
}