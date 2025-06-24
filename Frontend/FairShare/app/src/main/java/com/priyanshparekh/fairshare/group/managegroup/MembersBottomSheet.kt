package com.priyanshparekh.fairshare.group.managegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.model.User

class MembersBottomSheet : BottomSheetDialogFragment() {

    private lateinit var membersList: List<User>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemberListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fetch the list passed through arguments
        val usersJson = arguments?.getString("members_json")
        val type = object : TypeToken<List<User>>() {}.type
        membersList = Gson().fromJson(usersJson, type)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_bottom_sheet_members, container, false)

        recyclerView = view.findViewById(R.id.all_members_list)
        adapter = MemberListAdapter(membersList)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        return view
    }

    companion object {
        fun newInstance(members: List<User>): MembersBottomSheet {
            val fragment = MembersBottomSheet()
            return fragment
        }
    }

}