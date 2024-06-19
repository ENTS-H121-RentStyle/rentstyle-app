package com.example.rentstyle.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.UserRepository

class TransactionViewModel (private val userRepository: UserRepository): ViewModel() {
    suspend fun getAllOrder() = userRepository.getUserOrder()

    suspend fun getOrderById(orderId: String) = userRepository.getUserOrderById(orderId)
}