package com.priyanshparekh.fairshare.network

import com.priyanshparekh.fairshare.group.balances.notification.NotificationRequest
import com.priyanshparekh.fairshare.group.dashboard.DashboardDTO
import com.priyanshparekh.fairshare.model.ActivityLog
import com.priyanshparekh.fairshare.model.BalanceInfo
import com.priyanshparekh.fairshare.model.Expense
import com.priyanshparekh.fairshare.model.Group
import com.priyanshparekh.fairshare.model.GroupMember
import com.priyanshparekh.fairshare.model.PayData
import com.priyanshparekh.fairshare.model.UploadResponse
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.model.UserDevice
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {

    // Users
    @POST("/users")
    suspend fun addUser(@Body user: User): Response<User>

    @GET("/users/{username}")
    suspend fun getUserByUsername(@Path(value = "username") username: String): Response<User>

    @GET("/users")
    suspend fun searchUser(@Query(value = "query") query: CharSequence): Response<List<User>>



    //Groups

    @GET("/users/{user-id}/groups")
    suspend fun getGroups(@Path(value = "user-id") userId: Long): Response<List<Group>>

    @POST("/users/{user-id}/groups")
    suspend fun addGroup(@Path(value = "user-id") userId: Long, @Body group: Group): Response<Group>

    @POST("/groups/{group-id}/members")
    suspend fun addUsersToGroup(@Path(value = "group-id") groupId: Long, @Body newMembersList: ArrayList<User>): Response<List<GroupMember>>

    @GET("/groups/{group-id}/members")
    suspend fun getGroupMembers(@Path(value = "group-id") groupId: Long): Response<List<User>>

    // Expenses

    @POST("/groups/{group-id}/expenses")
    suspend fun addExpense(@Path(value = "group-id") groupId: Long, @Body expense: Expense): Response<Expense>

    @GET("/groups/{group-id}/expenses")
    suspend fun getGroupExpenses(@Path(value = "group-id") groupId: Long): Response<List<Expense>>

    @Multipart
    @POST("/groups/{group-id}/receipts")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<UploadResponse>


    // Activity Logs

    @POST("/groups/{group-id}/logs")
    suspend fun addActivityLog(@Path(value = "group-id") groupId: Long, @Body activityLog: ActivityLog): Response<ActivityLog>

    @GET("/groups/{group-id}/logs")
    suspend fun getActivityLogs(@Path(value = "group-id") groupId: Long): Response<List<ActivityLog>>

    // Balance Info

    @GET("groups/{group-id}/balance-info/{user-id}")
    suspend fun getBalanceInfo(@Path(value = "group-id") groupId: Long, @Path(value = "user-id") userId: Long): Response<List<BalanceInfo>>

    @GET("/groups/{group-id}/update-balance-info")
    suspend fun updateBalanceInfo(@Body newOwesToList: List<BalanceInfo>, @Path(value = "group-id") groupId: Long): Response<List<BalanceInfo>>

    @POST("/groups/{group-id}/pay")
    suspend fun payUser(@Body payData: PayData, @Path(value = "group-id") groupId: Long): Response<PayData>


    // Dashboard

    @GET("/groups/{group-id}/stats")
    suspend fun getDashboardStats(@Path(value = "group-id") groupId: Long): Response<DashboardDTO>

    // Notifications

    @POST("/register")
    suspend fun registerDevice(@Body userDevice: UserDevice): Response<UserDevice>

    @POST("/unregister")
    suspend fun unregisterDevice(@Query(value = "id") userId: String, @Query(value = "token") fcmToken: String): Response<String>

    @POST("/notify")
    suspend fun sendNotification(@Body request: NotificationRequest): Response<String>


    // Export Data

    @GET("/groups/{group-id}/export/excel")
    @Streaming
    suspend fun exportExcel(@Path(value = "group-id") groupId: Long): Response<ResponseBody>

    @GET("/groups/{group-id}/export/pdf")
    @Streaming
    suspend fun exportPdf(@Path(value = "group-id") groupId: Long): Response<ResponseBody>

}
