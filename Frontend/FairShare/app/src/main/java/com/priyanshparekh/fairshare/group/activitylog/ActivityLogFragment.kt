package com.priyanshparekh.fairshare.group.activitylog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.priyanshparekh.fairshare.databinding.FragmentActivityLogBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.model.ActivityLog
import com.priyanshparekh.fairshare.utils.Status

class ActivityLogFragment : Fragment() {

    private lateinit var binding: FragmentActivityLogBinding

    private val activityLogViewModel: ActivityLogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentActivityLogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupId = (activity as GroupActivity).groupId

        activityLogViewModel.getActivityLogs(groupId)

        val activityLogs = ArrayList<ActivityLog>()
        val adapter = ActivityLogAdapter(activityLogs)
        binding.activityLogs.adapter = adapter
        binding.activityLogs.layoutManager = LinearLayoutManager(requireContext())
        binding.activityLogs.setHasFixedSize(false)

        activityLogViewModel.activityLogs.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    activityLogs.clear()
                    activityLogs.addAll(status.data)

                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}