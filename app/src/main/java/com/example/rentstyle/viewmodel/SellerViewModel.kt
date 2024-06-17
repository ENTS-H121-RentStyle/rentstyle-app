package com.example.rentstyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.SellerRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SellerViewModel (private val sellerRepository: SellerRepository): ViewModel() {
    suspend fun getSellerData(userId: String) = sellerRepository.getSellerData(userId)
}