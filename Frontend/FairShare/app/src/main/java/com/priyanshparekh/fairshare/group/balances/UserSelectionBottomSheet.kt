package com.priyanshparekh.fairshare.group.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.priyanshparekh.fairshare.databinding.BottomSheetUserSelectionBinding
import com.priyanshparekh.fairshare.model.BalanceInfoItem

class UserSelectionBottomSheet(private val users: List<BalanceInfoItem>, private val mode: String, private val onSelectionDone: (List<BalanceInfoItem>) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetUserSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BottomSheetUserSelectionBinding.inflate(layoutInflater)
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

        binding.btnDone.text = mode

        val adapter = UserSelectionAdapter(users)
        binding.userSelectionList.adapter = adapter
        binding.userSelectionList.layoutManager = LinearLayoutManager(requireContext())
        binding.userSelectionList.setHasFixedSize(true)

        binding.btnDone.setOnClickListener {
            onSelectionDone(adapter.getSelectedUsers())
            dismiss()
        }
    }

}