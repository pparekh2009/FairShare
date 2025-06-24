package com.priyanshparekh.fairshare.group.managegroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.FragmentManageGroupBinding
import com.priyanshparekh.fairshare.databinding.LayoutExportOptionsBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.group.GroupViewModel
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.utils.Status

class ManageGroupFragment : Fragment() {

    private lateinit var binding: FragmentManageGroupBinding
    private val groupViewModel: GroupViewModel by activityViewModels()
    private val manageGroupViewModel: ManageGroupViewModel by viewModels()

    private var groupId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentManageGroupBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupMembers = ArrayList<User>()
        val displayedMembers = ArrayList<User>()

        val adapter = MemberListAdapter(groupMembers)

        groupId = (activity as GroupActivity).groupId

        binding.membersList.adapter = adapter
        binding.membersList.layoutManager = LinearLayoutManager(requireContext())
        binding.membersList.setHasFixedSize(true)

        groupViewModel.groupMembersStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    groupMembers.clear()
                    groupMembers.addAll(status.data)

                    if (groupMembers.size < 5) {
                        displayedMembers.addAll(groupMembers)
                    } else {
                        displayedMembers.addAll(groupMembers.take(4))
                        binding.btnSeeAll.visibility = View.VISIBLE
                        binding.btnSeeAll.text = "See All (${groupMembers.size})"
                    }

                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.btnSeeAll.setOnClickListener {
            val bottomSheet = MembersBottomSheet.newInstance(groupMembers)

            val args = Bundle()
            val usersJson = Gson().toJson(groupMembers)
            args.putString("members_json", usersJson)

            bottomSheet.arguments = args
            bottomSheet.show(parentFragmentManager, MembersBottomSheet::class.java.simpleName)
        }

        binding.btnAddMembers.setOnClickListener {
            showAddMembersDialog()
        }


        binding.exportDataLabel.setOnClickListener {
            showExportOptionsDialog()
        }

        manageGroupViewModel.downloadStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> binding.loading.visibility = View.GONE
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.data, Toast.LENGTH_SHORT).show()
                }
            }

        }

        manageGroupViewModel.exportDataStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    manageGroupViewModel.downloadFile(status.data)

                }
            }
        }
    }



    private fun showExportOptionsDialog() {
        val optionDialog = MaterialAlertDialogBuilder(requireContext()).create()

        val optionDialogView = LayoutExportOptionsBinding.inflate(layoutInflater)
        optionDialog.setView(optionDialogView.root)

        var exportType = ""
        optionDialogView.optionToggleGroup.addOnButtonCheckedListener { materialButtonToggleGroup, i, b ->
            val checkedBtnId = materialButtonToggleGroup.checkedButtonId

            when (checkedBtnId) {
                optionDialogView.btnPdf.id -> {
//                    optionDialogView.btnPdf.setBackgroundColor(resources.getColor(R.color.md_theme_primary, requireContext().theme))
                    exportType = "PDF"
                }
                optionDialogView.btnExcel.id -> {
                    exportType = "Excel"
                }
            }
        }

        optionDialogView.btnExport.setOnClickListener {
            if (exportType.isBlank()) {
                Toast.makeText(requireContext(), "Select a export type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            manageGroupViewModel.exportData(exportType, groupId)

            optionDialog.dismiss()
        }

        optionDialog.show()
    }

    private fun showAddMembersDialog() {
        findNavController().navigate(R.id.nav_add_members)
    }
}