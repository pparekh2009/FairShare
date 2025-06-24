package com.priyanshparekh.fairshare.group.balances

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.FragmentBalancesBinding
import com.priyanshparekh.fairshare.databinding.LayoutNotificationInputBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.group.balances.notification.NotificationRequest
import com.priyanshparekh.fairshare.model.BalanceInfo
import com.priyanshparekh.fairshare.model.BalanceInfoItem
import com.priyanshparekh.fairshare.model.Direction
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.OnRvItemClickListener
import com.priyanshparekh.fairshare.utils.Status

class BalancesFragment : Fragment() {

    private lateinit var binding: FragmentBalancesBinding

    private val balancesViewModel: BalancesViewModel by viewModels()

    private lateinit var userNames: Map<Long, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentBalancesBinding.inflate(layoutInflater)
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
        val groupName = (activity as GroupActivity).groupName
        val userId = requireContext().getSharedPreferences(Constants.PREF_USER, Context.MODE_PRIVATE).getLong(Constants.PrefKeys.KEY_USER_ID, -1L)

        userNames = (activity as GroupActivity).usernames

        val balanceInfo = ArrayList<BalanceInfo>()
        val owesTo = ArrayList<BalanceInfoItem>()
        val receivesFrom = ArrayList<BalanceInfoItem>()
        val balanceInfoList = ArrayList<BalanceInfoItem>()


        var simpleBalances = listOf(
            BalanceInfo(id = 1, groupId = 1, userId = 1, direction = Direction.OWES_TO, otherUserId = 2, amount = 50.0),
            BalanceInfo(id = 2, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 1, amount = 20.0),
            BalanceInfo(id = 3, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 3, amount = 30.0),
            BalanceInfo(id = 4, groupId = 1, userId = 3, direction = Direction.OWES_TO, otherUserId = 4, amount = 10.0),
            BalanceInfo(id = 5, groupId = 1, userId = 4, direction = Direction.OWES_TO, otherUserId = 1, amount = 10.0)
        )

        var complexBalances = listOf(
            BalanceInfo(id = 10, groupId = 1, userId = 1, direction = Direction.OWES_TO, otherUserId = 2, amount = 100.0),
            BalanceInfo(id = 11, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 3, amount = 100.0),
            BalanceInfo(id = 12, groupId = 1, userId = 3, direction = Direction.OWES_TO, otherUserId = 1, amount = 100.0),

            BalanceInfo(id = 13, groupId = 1, userId = 1, direction = Direction.OWES_TO, otherUserId = 4, amount = 50.0),
            BalanceInfo(id = 14, groupId = 1, userId = 4, direction = Direction.OWES_TO, otherUserId = 5, amount = 50.0),
            BalanceInfo(id = 15, groupId = 1, userId = 5, direction = Direction.OWES_TO, otherUserId = 6, amount = 50.0),
            BalanceInfo(id = 16, groupId = 1, userId = 6, direction = Direction.OWES_TO, otherUserId = 1, amount = 50.0),

            BalanceInfo(id = 17, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 5, amount = 20.0),
            BalanceInfo(id = 18, groupId = 1, userId = 5, direction = Direction.OWES_TO, otherUserId = 2, amount = 5.0),
        )

        var testBalances1 = listOf(
            BalanceInfo(id = 1, groupId = 1, userId = 1, direction = Direction.OWES_TO, otherUserId = 2, amount = 40.0),
            BalanceInfo(id = 2, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 3, amount = 30.0),
            BalanceInfo(id = 3, groupId = 1, userId = 3, direction = Direction.OWES_TO, otherUserId = 1, amount = 20.0)
        )

        var testBalances2 = listOf(
            BalanceInfo(id = 1, groupId = 1, userId = 1, direction = Direction.OWES_TO, otherUserId = 2, amount = 25.0),
            BalanceInfo(id = 2, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 3, amount = 25.0),
            BalanceInfo(id = 3, groupId = 1, userId = 3, direction = Direction.OWES_TO, otherUserId = 4, amount = 25.0),
            BalanceInfo(id = 4, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 4, amount = 10.0)
        )

        var testBalances3 = listOf(
            BalanceInfo(id = 1, groupId = 1, userId = 1, direction = Direction.OWES_TO, otherUserId = 2, amount = 100.0),
            BalanceInfo(id = 2, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 1, amount = 60.0),
            BalanceInfo(id = 3, groupId = 1, userId = 2, direction = Direction.OWES_TO, otherUserId = 3, amount = 40.0),
            BalanceInfo(id = 4, groupId = 1, userId = 3, direction = Direction.OWES_TO, otherUserId = 4, amount = 40.0),
            BalanceInfo(id = 5, groupId = 1, userId = 4, direction = Direction.OWES_TO, otherUserId = 1, amount = 20.0)
        )

        binding.btnSimplify.setOnClickListener {

            var oldOwesToList = balanceInfo.filter { balanceInfo -> balanceInfo.direction == Direction.OWES_TO }

//            if (oldOwesToList.size < 2) {
//                Snackbar.make(binding.root, "No simplification Possible", Snackbar.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            val previousOwesToList = testBalances3
            do {
                val previousSize = testBalances3.size
                testBalances3 = simplifyBalances(testBalances3, groupId)
            } while (testBalances3.size < previousSize)

//            val previousOwesToList = oldOwesToList
//            do {
//                val previousSize = oldOwesToList.size
//                oldOwesToList = simplifyBalances(oldOwesToList, groupId)
//            } while (oldOwesToList.size < previousSize)

//            if (testBalances3.size == previousOwesToList.size) {
//                Snackbar.make(binding.root, "No simplification Possible", Snackbar.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            showSimplifyView(previousOwesToList, testBalances3, userNames) {
                balancesViewModel.updateBalanceInfo(oldOwesToList, groupId)
            }
        }

        balancesViewModel.updateBalanceInfo.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> { }
                is Status.SUCCESS -> {
                    balancesViewModel.getBalanceInfo(groupId, userId)
                }
            }
        }

        balancesViewModel.getBalanceInfo(groupId, userId)


        val adapter = BalancesAdapter(balanceInfoList, object : OnRvItemClickListener {
            override fun onItemClick(position: Int) {
                if (balanceInfoList[position].buttonText == "Pay") {
                    showUserSelectionList(owesTo, "Pay", groupName)
                } else if (balanceInfoList[position].buttonText == "Request") {
                    showUserSelectionList(receivesFrom, "Request", groupName)
                }
            }
        })
        binding.balancesList.adapter = adapter
        binding.balancesList.layoutManager = LinearLayoutManager(requireContext())
        binding.balancesList.setHasFixedSize(true)

        balancesViewModel.balanceInfoStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    balanceInfo.clear()
                    balanceInfo.addAll(status.data)

                    for (balance in balanceInfo) {
                        if (balance.direction == Direction.OWES_TO) {
                            owesTo.add(BalanceInfoItem(
                                balance.otherUserId,
                                userNames[balance.otherUserId],
                                balance.amount,
                                null,
                                null,
                                BalancesAdapter.ItemViewType.VIEW_TYPE_ITEM))
                        } else {
                            receivesFrom.add(BalanceInfoItem(
                                balance.otherUserId,
                                userNames[balance.otherUserId],
                                balance.amount,
                                null,
                                null,
                                BalancesAdapter.ItemViewType.VIEW_TYPE_ITEM))
                        }
                    }

                    if (owesTo.size > 0) {
                        balanceInfoList.add(BalanceInfoItem(null, null, null, "Pay To", null, BalancesAdapter.ItemViewType.VIEW_TYPE_HEADER))
                        balanceInfoList.addAll(owesTo)
                        balanceInfoList.add(BalanceInfoItem(null, null, null, null, "Pay", BalancesAdapter.ItemViewType.VIEW_TYPE_BUTTON))
                    }

                    if (receivesFrom.size > 0) {
                        balanceInfoList.add(BalanceInfoItem(null, null, null, "Receive From", null, BalancesAdapter.ItemViewType.VIEW_TYPE_HEADER))
                        balanceInfoList.addAll(receivesFrom)
                        balanceInfoList.add(BalanceInfoItem(null, null, null, null, "Request", BalancesAdapter.ItemViewType.VIEW_TYPE_BUTTON))
                    }

                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun showUserSelectionList(userList: ArrayList<BalanceInfoItem>, mode: String, groupName: String) {
        val sheet = UserSelectionBottomSheet(userList, mode) { selectedUsers ->

            if (mode == "Request") {
                showRequestMessageDialog(userList.size, groupName) { message ->

                    val messageData = mapOf(
                        Pair("title", "New Message!"),
                        Pair("message", message)
                    )

                    val notificationRequest = NotificationRequest(userList.map(BalanceInfoItem::userId), messageData)

                    balancesViewModel.notifyUsers(notificationRequest)
                }
            }

            if (mode == "Pay") {
                for (user in selectedUsers) {
                    val bundle = Bundle()
                    bundle.putString("groupName", groupName)
                    bundle.putDouble("amount", user.amount!!)
                    bundle.putString("username", user.username)
                    bundle.putLong("userId", user.userId!!)
                    findNavController().navigate(R.id.nav_payment, bundle)
                }
            }
        }

        sheet.show(parentFragmentManager, "UserSelectionBottomSheet")
    }

    private fun showRequestMessageDialog(noOfMembers: Int, groupName: String, notify: (message: String) -> Unit) {
        val dialog = MaterialAlertDialogBuilder(requireContext()).create()

        val dialogBinding = LayoutNotificationInputBinding.inflate(layoutInflater)

        dialogBinding.messageInput.setText("Hey! Just wanted to remind you about the expenses from our group \"$groupName\". Let me know when you can settle it.")

        dialogBinding.header.text = "Notify $noOfMembers members"
        dialogBinding.btnNotify.setOnClickListener {
            val message = dialogBinding.messageInput.text?.toString() ?: ""
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            notify(dialogBinding.messageInput.text?.toString() ?: "")

            dialog.dismiss()
        }

        dialog.setView(dialogBinding.root)

        dialog.show()
    }

    private fun showSimplifyView(beforeList: List<BalanceInfo>, afterList: List<BalanceInfo>, usernames: Map<Long, String>, updateDb: () -> Unit) {
        val viewStub = binding.simplifyViewStub.inflate()

        val beforeListRv = viewStub.findViewById<RecyclerView>(R.id.before_list)
        val afterListRv = viewStub.findViewById<RecyclerView>(R.id.after_list)
        val btnApplyChanges = viewStub.findViewById<MaterialButton>(R.id.btn_apply)

        val beforeListAdapter = SimplifiedBalanceAdapter(beforeList, usernames)
        beforeListRv.adapter = beforeListAdapter
        beforeListRv.layoutManager = LinearLayoutManager(requireContext())
        beforeListRv.setHasFixedSize(true)

        val afterListAdapter = SimplifiedBalanceAdapter(afterList, usernames)
        afterListRv.adapter = afterListAdapter
        afterListRv.layoutManager = LinearLayoutManager(requireContext())
        afterListRv.setHasFixedSize(true)

        btnApplyChanges.setOnClickListener {
            viewStub.visibility = View.GONE

            updateDb()
        }
    }

    private fun simplifyBalances(balanceList: List<BalanceInfo>, groupId: Long): List<BalanceInfo> {
        val balancesMap = HashMap<Pair<Long, Long>, Double>()

        for (balance in balanceList) {
            balancesMap[Pair(balance.userId, balance.otherUserId)] = balance.amount
        }

        Log.d("TAG", "simplifyBalances: old balance map: ")
        for (key in balancesMap.keys) {
            Log.d("TAG", "simplifyBalances: $key: ${balancesMap[key]}")
        }

        val newBalanceMap1 = HashMap<Pair<Long, Long>, Double>()
        for (key in balancesMap.keys) {
            val inverseKey = Pair(key.second, key.first)
            if (balancesMap.containsKey(inverseKey)) {
                if (balancesMap[key]!! > balancesMap[inverseKey]!!) {
                    newBalanceMap1[key] = balancesMap[key]!! - balancesMap[inverseKey]!!
//                    balancesMap.remove(inverseKey)
                } else {
                    newBalanceMap1[inverseKey] = balancesMap[inverseKey]!! - balancesMap[key]!!
//                    balancesMap.remove(key)
                }
            } else {
                newBalanceMap1[key] = balancesMap[key]!!
            }
        }

        Log.d("TAG", "simplifyBalances: new balance map 1: ")
        for (key in newBalanceMap1.keys) {
            Log.d("TAG", "simplifyBalances: $key: ${newBalanceMap1[key]}")
        }

        val newBalanceMap2 = HashMap<Pair<Long, Long>, Double>(newBalanceMap1)
        outerloop@ for (key1 in newBalanceMap1.keys) {
            innerloop@ for (key2 in newBalanceMap1.keys) {
                if (key1 != key2) {
                    if (key1.second == key2.first) {
//                        Log.d("TAG", "simplifyBalances: key 1: $key1")
//                        Log.d("TAG", "simplifyBalances: key 2: $key2")
                        val key = Pair(key1.first, key2.second)
//                        Log.d("TAG", "simplifyBalances: key: $key")
//                        if (key != key1 && key != key2) {
                        if (!newBalanceMap2.containsKey(key)) {
//                            if (newBalanceMap1.containsKey(key)) {
                                if (newBalanceMap1[key1] == newBalanceMap1[key2]) {
                                    if (newBalanceMap1[key1] != 0.0) {
                                        newBalanceMap2[key] = newBalanceMap1[key1]!!
                                    }
                                    newBalanceMap2.remove(key1)
                                    newBalanceMap2.remove(key2)
                                    break@outerloop
                                }
//                            }
                        }
                    }
                }
            }
        }

        Log.d("TAG", "simplifyBalances: new balance map 2: ")
        for (key in newBalanceMap2.keys) {
            Log.d("TAG", "simplifyBalances: $key: ${newBalanceMap2[key]}")
        }

        val newBalanceList = ArrayList<BalanceInfo>()
        for (newBalance in newBalanceMap2) {
            newBalanceList.add(BalanceInfo(null, groupId, newBalance.key.first, Direction.OWES_TO, newBalance.key.second, newBalance.value))
        }

        Log.d("TAG", "simplifyBalances: ========================================")

        return newBalanceList

//        val balanceGraph = Graph()
//        Log.d("TAG", "simplifyBalances: balanceGraph: $balanceGraph")
//
//        for (entry in balanceList) {
//            val triple = Triple(entry.userId, entry.otherUserId, entry.amount)
//            Log.d("TAG", "simplifyBalances: triple: $triple")
//            balanceGraph.addEdge(entry.userId, entry.otherUserId, entry.amount)
//        }
//
//        balanceGraph.show()
    }
}