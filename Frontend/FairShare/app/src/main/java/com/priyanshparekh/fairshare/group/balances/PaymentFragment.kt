package com.priyanshparekh.fairshare.group.balances

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.priyanshparekh.fairshare.databinding.FragmentPaymentBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.group.activitylog.ActivityLogViewModel
import com.priyanshparekh.fairshare.model.ActivityLog
import com.priyanshparekh.fairshare.model.PayData
import com.priyanshparekh.fairshare.utils.ActivityLogs
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.Status

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding

    private val balancesViewModel: BalancesViewModel by viewModels()
    private val activityLogViewModel: ActivityLogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentPaymentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupName = arguments?.getString("groupName")
        val userName = arguments?.getString("username")
        val amount = arguments?.getDouble("amount") ?: 0.0
        val userId = arguments?.getLong("userId")

        val groupId = (activity as GroupActivity).groupId

        val payerId = requireContext().getSharedPreferences(Constants.PREF_USER, Context.MODE_PRIVATE).getLong(Constants.PrefKeys.KEY_USER_ID, -1L)
        val payerName = requireContext().getSharedPreferences(Constants.PREF_USER, Context.MODE_PRIVATE).getString(Constants.PrefKeys.KEY_USERNAME, "")

        binding.payeeNameLabel.text = "Paying $userName"
        binding.amountLabel.text = "You owe: $$amount"
        binding.groupNameLabel.text = "Group: $groupName"
        
        binding.amountInput.setText(amount.toString())

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.btnPay.setOnClickListener { 
            val amountToPay = binding.amountInput.text?.toString()?.toDouble() ?: 0.0
            val note = binding.noteInput.text?.toString() ?: ""

            if (amountToPay <= 0.0 || amountToPay > amount) {
                Snackbar.make(binding.root, "Enter amount between 0.0 and $amount", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val payData = PayData(payerId, userId!!, amountToPay, note)
            balancesViewModel.payUser(payData, groupId)
        }

        balancesViewModel.payUserStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_SHORT).show()

                    val payData = status.data
                    activityLogViewModel.addActivityLog(groupId, ActivityLog(groupId = groupId, log = ActivityLogs.paymentMade(payerName!!, userName!!, payData.amount.toString(), payData.note)))

                    findNavController().popBackStack()
                }
            }
        }
    }
}