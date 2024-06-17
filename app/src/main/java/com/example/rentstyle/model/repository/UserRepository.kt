package com.example.rentstyle.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.remote.response.SellerResponseData
import com.example.rentstyle.model.remote.response.UserResponseData
import com.example.rentstyle.model.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userId: String
) {
    private val userResultData = MediatorLiveData<DataResult<UserResponseData>>()
    private val statusResult = MediatorLiveData<StatusResult>()

    suspend fun updateUserProfile(userName: RequestBody,
                                  birthDate: RequestBody,
                                  address: RequestBody,
                                  phone: RequestBody,
                                  gender: RequestBody,
                                  file: MultipartBody.Part): LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading

        try {
            val response = apiService.updateUserData(userId, userName, birthDate, address, phone, gender, file)

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

    companion object {

        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userId: String
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userId)
            }.also { instance = it }
    }
}