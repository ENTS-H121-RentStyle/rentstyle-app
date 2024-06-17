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

class SellerRepository private constructor(
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

    companion object {

        @Volatile
        private var instance: SellerRepository? = null

        fun getInstance(
            apiService: ApiService
        ): SellerRepository =
            instance ?: synchronized(this) {
                instance ?: SellerRepository(apiService)
            }.also { instance = it }
    }
}