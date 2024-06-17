package com.example.rentstyle.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.SellerRepository

class ProfileViewModel (private val sellerRepository: SellerRepository): ViewModel() {
    suspend fun getSellerData(userId: String) = sellerRepository.getSellerData(userId)
}