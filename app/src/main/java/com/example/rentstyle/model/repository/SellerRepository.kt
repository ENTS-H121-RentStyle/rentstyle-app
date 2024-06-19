package com.example.rentstyle.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.remote.response.SellerResponseData
import com.example.rentstyle.model.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SellerRepository (
    private val apiService: ApiService,
){
    private val sellerResultData = MediatorLiveData<DataResult<SellerResponseData>>()
    private val statusResult = MediatorLiveData<StatusResult>()

    suspend fun getSellerData (userId: String): LiveData<DataResult<SellerResponseData>> {
        sellerResultData.value = DataResult.Loading

        try {
            val response = apiService.getSellerData(userId)

            if (response.body() != null) {
                val sellerLiveData: LiveData<SellerResponseData> = MutableLiveData(response.body())
                sellerResultData.addSource(sellerLiveData) { data ->
                    sellerResultData.value = DataResult.Success(data)
                }
            } else {
                sellerResultData.value = DataResult.Error("null")
            }
        } catch (e: Exception) {
            sellerResultData.value = DataResult.Error(e.toString())
        }

        return sellerResultData
    }

    suspend fun createSellerAccount (file: MultipartBody.Part,
                                     sellerName: RequestBody,
                                     userId: RequestBody,
                                     address: RequestBody,
                                     desc: RequestBody,
                                     city: RequestBody) : LiveData<DataResult<SellerResponseData>> {
        sellerResultData.value = DataResult.Loading

        try {
            val response = apiService.createNewSeller(file, sellerName, userId, address, desc, city)

            if (response.code() == 201) {
                val sellerLiveData: LiveData<SellerResponseData> = MutableLiveData(response.body())
                sellerResultData.addSource(sellerLiveData) { data ->
                    sellerResultData.value = DataResult.Success(data)
                }
            } else {
                sellerResultData.value = DataResult.Error("Error registering account")
            }
        } catch (e: Exception) {
            sellerResultData.value = DataResult.Error(e.toString())
        }

        return sellerResultData
    }

    suspend fun updateSellerProfile (sellerId: String,
                                     sellerName: RequestBody,
                                     address: RequestBody,
                                     desc: RequestBody): LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading

        try {
            val response = apiService.updateSellerData(sellerId, sellerName, address, desc)

            if (response.code() == 200) {
                statusResult.value = StatusResult.Success("Success update seller data")
            } else {
                statusResult.value = StatusResult.Error("Failed update seller data")
            }
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
        }

        return statusResult
    }
}