package com.priyanshparekh.fairshare.group.managegroup

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.priyanshparekh.fairshare.databinding.LayoutAddMemberDialogBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.group.GroupViewModel
import com.priyanshparekh.fairshare.group.activitylog.ActivityLogViewModel
import com.priyanshparekh.fairshare.model.ActivityLog
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.utils.ActivityLogs
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.Status

class AddMembersFragment : Fragment() {

    private lateinit var binding: LayoutAddMemberDialogBinding
    private val manageGroupViewModel: ManageGroupViewModel by viewModels()
    private val activityLogViewModel: ActivityLogViewModel by viewModels()
    private val groupViewModel: GroupViewModel by activityViewModels()

    private var groupId: Long = -1L
    private var groupName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutAddMemberDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = (activity as GroupActivity).groupId
        groupName = (activity as GroupActivity).groupName

        binding.groupNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    return
                }

                if (p0.length == 2) {
                    val query = p0.toString()

                    // get user containing p0
                    manageGroupViewModel.getUserSearchResults(query)

                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        val searchResults = ArrayList<User>()
        val newMembersList = ArrayList<User>()

        manageGroupViewModel.searchStatus.observe(requireActivity()) { status ->
            when (status) {
                is Status.ERROR -> Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                is Status.LOADING -> {}
                is Status.SUCCESS -> {
                    searchResults.clear()
                    searchResults.addAll(status.data)

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, searchResults)
                    binding.groupNameInput.setAdapter(adapter)
                    binding.groupNameInput.threshold = 2

                    binding.groupNameInput.showDropDown()
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnDone.setOnClickListener {
            val groupId = (activity as GroupActivity).groupId
            if (groupId != -1L) {
                manageGroupViewModel.addUsersToGroup(groupId, newMembersList)
            }
        }

        manageGroupViewModel.addMembersStatus.observe(requireActivity()) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    val memberName = requireContext().getSharedPreferences(Constants.PREF_USER, Context.MODE_PRIVATE).getString(Constants.PrefKeys.KEY_NAME, "")!!

                    for (user in newMembersList) {
                        activityLogViewModel.addActivityLog(groupId, ActivityLog(groupId = groupId, log = ActivityLogs.memberAdded(memberName, user.name, groupName)))
                    }

                    groupViewModel.getGroupMembers(groupId)

                    Toast.makeText(requireContext(), status.data, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }

        val memberListAdapter = MemberListAdapter(newMembersList)
        binding.newMembersList.adapter = memberListAdapter
        binding.newMembersList.layoutManager = LinearLayoutManager(requireContext())
        binding.newMembersList.setHasFixedSize(true)

        binding.groupNameInput.setOnItemClickListener { parent, view, position, id ->
            val user = parent.getItemAtPosition(position) as User

            newMembersList.add(user)
            memberListAdapter.notifyDataSetChanged()
            binding.groupNameInput.dismissDropDown()
            binding.groupNameInput.setText("")
        }
    }
}