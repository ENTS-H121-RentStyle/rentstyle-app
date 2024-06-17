package com.example.rentstyle.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.SellerRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SellerRegisterViewModel (private val sellerRepository: SellerRepository): ViewModel() {
    suspend fun registerSellerAccount(file: MultipartBody.Part, sellerName: RequestBody,
                                      userId: RequestBody, address: RequestBody,
                                      desc: RequestBody, city: RequestBody
    ) = sellerRepository
        .createSellerAccount(file, sellerName, userId, address, desc, city)
}