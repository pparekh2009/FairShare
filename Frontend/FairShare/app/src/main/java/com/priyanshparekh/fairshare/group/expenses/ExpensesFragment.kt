package com.priyanshparekh.fairshare.group.expenses

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.FragmentExpensesBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.group.GroupViewModel
import com.priyanshparekh.fairshare.group.activitylog.ActivityLogViewModel
import com.priyanshparekh.fairshare.model.ActivityLog
import com.priyanshparekh.fairshare.model.Expense
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.utils.ActivityLogs
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.Status
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class ExpensesFragment : Fragment() {

    private lateinit var binding: FragmentExpensesBinding

    private val groupViewModel: GroupViewModel by activityViewModels()
    private val expenseViewModel: ExpenseViewModel by viewModels()
    private val activityLogViewModel: ActivityLogViewModel by viewModels()

    private val groupMembers = ArrayList<User>()
    private val splitAmongAdapter = SplitAmongAdapter(groupMembers)

    private var selectedFileUri: Uri? = null

    private lateinit var filePickerLauncher: ActivityResultLauncher<String>
    private var onFilePickedCallback: ((Uri, String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentExpensesBinding.inflate(layoutInflater)
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

        setupFilePicker()

        expenseViewModel.getExpenses(groupId)

        val usernames = (activity as GroupActivity).usernames

        val expenses = ArrayList<Expense>()
        val adapter = ExpenseAdapter(expenses, usernames)
        binding.groupExpenseList.adapter = adapter
        binding.groupExpenseList.layoutManager = LinearLayoutManager(requireContext())
        binding.groupExpenseList.setHasFixedSize(true)

        expenseViewModel.expensesStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    expenses.clear()
                    expenses.addAll(status.data)

                    var total = 0.0
                    expenses.iterator().forEach {
                        total += it.amount
                    }

                    binding.totalValue.text = "$$total"

                    adapter.notifyDataSetChanged()
                }
            }
        }

        groupViewModel.groupMembersStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is Status.ERROR -> Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                is Status.LOADING -> { }
                is Status.SUCCESS -> {
                    groupMembers.clear()
                    groupMembers.addAll(status.data)

                    for (user in status.data) {
                        usernames[user.id!!] = user.name
                    }

                    splitAmongAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.btnAddExpense.setOnClickListener {
            showAddExpenseDialog(groupId) { uri, _ ->
                selectedFileUri = uri
            }
        }

        expenseViewModel.addExpenseStatus.observe(viewLifecycleOwner) { status ->
            when(status) {
                is Status.ERROR -> {
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> { }
                is Status.SUCCESS -> {
                    expenseViewModel.getExpenses(groupId)

                    val expense = status.data
                    val userName = requireContext().getSharedPreferences(Constants.PREF_USER, Context.MODE_PRIVATE).getString(Constants.PrefKeys.KEY_NAME, "")!!
                    
                    activityLogViewModel.addActivityLog(groupId, ActivityLog(groupId = groupId, log = ActivityLogs.expenseAdded(userName, expense.name, expense.amount.toString())))
                }
            }
        }
    }

    private fun setupFilePicker() {
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val fileName = getFileNameFromUri(it)
                onFilePickedCallback?.invoke(it, fileName ?: "Unnamed")
            }
        }
    }

    private fun launchFilePicker(callback: (Uri, String) -> Unit) {
        onFilePickedCallback = callback
        filePickerLauncher.launch("*/*")
    }

    private fun showAddExpenseDialog(groupId: Long, onFilePicked: (Uri, String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_expense_dialog, null)

        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.expense_input)
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.amount_input)
        val noteInput = dialogView.findViewById<TextInputEditText>(R.id.note_input)
        val splitAmongList = dialogView.findViewById<RecyclerView>(R.id.split_among_list)
        val btnAddReceipt = dialogView.findViewById<MaterialButton>(R.id.btn_add_receipt)
        val receiptFileName = dialogView.findViewById<TextView>(R.id.receipt_name)
        val btnRemoveFile = dialogView.findViewById<MaterialButton>(R.id.btn_remove_receipt)

        btnRemoveFile.setOnClickListener {
            btnAddReceipt.visibility = View.VISIBLE

            btnRemoveFile.visibility = View.GONE
            receiptFileName.visibility = View.GONE

            receiptFileName.text = ""

            selectedFileUri = null
        }

        btnAddReceipt.setOnClickListener {
            launchFilePicker { uri, s ->
                btnAddReceipt.visibility = View.GONE

                btnRemoveFile.visibility = View.VISIBLE
                receiptFileName.visibility = View.VISIBLE

                receiptFileName.text = s

                onFilePicked(uri, s)
            }
        }

        splitAmongList.layoutManager = LinearLayoutManager(requireContext())
        splitAmongList.adapter = splitAmongAdapter
        splitAmongList.setHasFixedSize(true)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Expense")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString()
                val amount = amountInput.text.toString().toDoubleOrNull()
                val note = noteInput.text.toString()
                val selectedUsers = splitAmongAdapter.getSelectedUsers()

                if (name.isNotBlank() && amount != null && selectedUsers.isNotEmpty()) {
                    val createdAt = System.currentTimeMillis()
                    val currentUserId = requireContext().getSharedPreferences(Constants.PREF_USER, Context.MODE_PRIVATE).getLong(Constants.PrefKeys.KEY_USER_ID, -1L)
                    if (currentUserId != -1L) {
                        val expense = Expense(
                            id = null,
                            groupId = groupId,
                            name = name,
                            amount = amount,
                            note = note,
                            paidBy = currentUserId,
                            createdAt = createdAt,
                            receiptUrl = null,
                            splitBetween = selectedUsers
                    )

                        if (selectedFileUri != null) {
                            expenseViewModel.uploadReceipt(groupId, prepareFilePart(requireContext(), selectedFileUri!!, "file"), expense)
                        } else {
                            expenseViewModel.addExpense(groupId, expense)
                        }

                        splitAmongAdapter.unselectAll()

                    }
                } else {
                    Toast.makeText(requireContext(), "Fill all fields correctly", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                splitAmongAdapter.unselectAll()

                selectedFileUri = null
            }
            .create()

        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog.show()

    }

    private fun prepareFilePart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
        val contentResolver = context.contentResolver
        val fileType = contentResolver.getType(uri) ?: "application/octet-stream"
        val inputStream = contentResolver.openInputStream(uri) ?: throw IOException("Cannot open input stream")

        val tempFile = File.createTempFile("file", null, context.cacheDir)
        tempFile.outputStream().use { output -> inputStream.copyTo(output) }

        val requestFile = tempFile.asRequestBody(fileType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex != -1) {
                    fileName = it.getString(columnIndex)
                }
            }
        }

        // Fallback to the last path segment if the file name wasn't found
        return fileName ?: uri.lastPathSegment
    }

}