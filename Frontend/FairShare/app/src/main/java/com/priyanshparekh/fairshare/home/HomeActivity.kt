package com.priyanshparekh.fairshare.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.ActivityHomeBinding
import com.priyanshparekh.fairshare.databinding.LayoutAddGroupDialogBinding
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.group.activitylog.ActivityLogViewModel
import com.priyanshparekh.fairshare.model.ActivityLog
import com.priyanshparekh.fairshare.model.Group
import com.priyanshparekh.fairshare.settings.SettingsActivity
import com.priyanshparekh.fairshare.utils.ActivityLogs
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.OnRvItemClickListener
import com.priyanshparekh.fairshare.utils.Status

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val activityLogViewModel: ActivityLogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolBar)
        window.statusBarColor = resources.getColor(R.color.md_theme_primary, theme)

        if (!isNotificationPermissionGranted(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission(this)
            }
        }

        val userId = getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE).getLong(Constants.PrefKeys.KEY_USER_ID, -1L)
        val userName = getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE).getString(Constants.PrefKeys.KEY_NAME, "")!!
        val profilePic = getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE).getString(Constants.PrefKeys.KEY_PROFILE_PIC, "")!!

        binding.toolBar.title = userName

        val decodedBytes = Base64.decode(profilePic, Base64.NO_WRAP)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        binding.toolBar.navigationIcon = BitmapDrawable(resources, getCircularBitmap(bitmap))

        homeViewModel.getGroupList(userId)

        val groupList = ArrayList<Group>()
        val adapter = GroupListAdapter(groupList, object : OnRvItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@HomeActivity, GroupActivity::class.java)
                Log.d("TAG", "onCreate: homeActivity: group list size: ${groupList.size}")
                intent.putExtra("groupName", groupList[position].name)
                intent.putExtra("groupId", groupList[position].id)
                intent.putExtra("position", position)
                startActivity(intent)
            }
        })

        homeViewModel.groupListStatus.observe(this) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    groupList.clear()
                    groupList.addAll(status.data)
                    adapter.notifyDataSetChanged()
                }
            }
        }



        binding.groupList.adapter = adapter
        binding.groupList.layoutManager = LinearLayoutManager(this)
        binding.groupList.setHasFixedSize(true)

        var addGroupDialog: AlertDialog? = null
        binding.btnAddGroup.setOnClickListener {
            addGroupDialog = showAddGroupDialog(userId)
        }

        binding.toolBar.setNavigationOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        homeViewModel.addGroupStatus.observe(this) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    homeViewModel.getGroupList(userId)

                    binding.loading.visibility = View.GONE

                    addGroupDialog?.dismiss()

                    val group = status.data
                    val memberName = getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE).getString(Constants.PrefKeys.KEY_NAME, "")!!

                    activityLogViewModel.addActivityLog(group.id!!, ActivityLog(groupId = group.id, log = ActivityLogs.groupCreated(memberName, group.name)))
                }
            }
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        return output
    }

    private fun showAddGroupDialog(userId: Long): AlertDialog {
        val dialogBinding = LayoutAddGroupDialogBinding.inflate(layoutInflater)

        val addGroupDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        addGroupDialog.show()

        dialogBinding.btnCancel.setOnClickListener {
            addGroupDialog.dismiss()
        }

        dialogBinding.btnAdd.setOnClickListener {
            val name = dialogBinding.groupNameInput.text?.toString()

            if (name.isNullOrEmpty()) {
                dialogBinding.groupNameInputLayout.error = "Enter Group name"
                return@setOnClickListener
            }

            val group = Group(name)

            homeViewModel.addGroup(userId, group)
        }

        return addGroupDialog
    }

    private fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true // Automatically granted below Android 13
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission(activity: Activity, requestCode: Int = 1001) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            requestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val settingPrefs = getSharedPreferences(Constants.PREF_SETTING, MODE_PRIVATE)
        val editor = settingPrefs.edit()

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                editor.putBoolean(Constants.PrefKeys.KEY_NOTIFICATION, true)
                Log.d("Permission", "Notification permission granted")
            } else {
                editor.putBoolean(Constants.PrefKeys.KEY_NOTIFICATION, false)
                Log.d("Permission", "Notification permission denied")
            }
        }

        editor.apply()
    }
}