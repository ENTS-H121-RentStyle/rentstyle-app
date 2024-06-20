package com.example.rentstyle.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.remote.response.ResponseOrderItem
import com.example.rentstyle.model.remote.response.UserResponseData
import com.example.rentstyle.model.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(
    private val apiService: ApiService,
    private val userId: String
) {
    private val userResultData = MediatorLiveData<DataResult<UserResponseData>>()
    private val statusResult = MediatorLiveData<StatusResult>()
    private val userOrderData = MediatorLiveData<DataResult<List<ResponseOrderItem>>>()
    private val userOrder = MediatorLiveData<DataResult<ResponseOrderItem>>()

    suspend fun updateUserProfile(
        userName: RequestBody,
        birthDate: RequestBody,
        address: RequestBody,
        phone: RequestBody,
        gender: RequestBody,
        file: MultipartBody.Part
    ): LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading

        try {
            val response =
                apiService.updateUserData(userId, userName, birthDate, address, phone, gender, file)

            if (response.code() == 200) {
                statusResult.value = StatusResult.Success("Success update user data")
            } else {
                statusResult.value = StatusResult.Error("Failed update user data")
            }
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
        }

        return statusResult
    }

    suspend fun getUserData(): LiveData<DataResult<UserResponseData>> {
        userResultData.value = DataResult.Loading

        try {
            val response = apiService.getUserData(userId)

            if (response.body() != null) {
                val userLiveData: LiveData<UserResponseData> = MutableLiveData(response.body())
                userResultData.addSource(userLiveData) { data ->
                    userResultData.value = DataResult.Success(data)
                }
            } else {
                userResultData.value = DataResult.Error("null")
            }
        } catch (e: Exception) {
            userResultData.value = DataResult.Error(e.toString())
        }

        return userResultData
    }

    suspend fun getUserOrder(): LiveData<DataResult<List<ResponseOrderItem>>> {
        userOrderData.value = DataResult.Loading

        try {
            val response = apiService.getOrderByUserId(userId)

            if (response.code() == 200) {
                val orderLiveData: LiveData<List<ResponseOrderItem>> =
                    MutableLiveData(response.body())
                userOrderData.addSource(orderLiveData) { data ->
                    userOrderData.value = DataResult.Success(data)
                }
            }
        } catch (e: Exception) {
            userOrderData.value = DataResult.Error(e.toString())
        }

        return userOrderData
    }

    suspend fun getUserOrderById(orderId: String): LiveData<DataResult<ResponseOrderItem>> {
        userOrder.value = DataResult.Loading

        try {
            val response = apiService.getOrderByOrderId(orderId)

            if (response.code() == 200) {
                val orderLiveData: LiveData<ResponseOrderItem> = MutableLiveData(response.body())
                userOrder.addSource(orderLiveData) { data ->
                    userOrder.value = DataResult.Success(data)
                }
            } else {
                userOrder.value = DataResult.Error("Fail to get order data")
            }
        } catch (e: Exception) {
            userOrder.value = DataResult.Error(e.toString())
        }

        return userOrder
    }
}