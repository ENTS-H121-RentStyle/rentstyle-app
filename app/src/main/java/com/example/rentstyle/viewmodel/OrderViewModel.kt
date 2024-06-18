package com.example.rentstyle.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.ProductRepository
import okhttp3.RequestBody

class OrderViewModel(private val productRepository: ProductRepository): ViewModel() {
    suspend fun makeOrder(productId: RequestBody,
                          userId: RequestBody,
                          orderDate: RequestBody,
                          returnDate: RequestBody,
                          rentDuration: RequestBody,
                          serviceFee: RequestBody,
                          deposit: RequestBody,
                          rentPrice: RequestBody,
                          totalPayment: RequestBody) = productRepository
                              .makeOrder(productId, userId, orderDate, returnDate, rentDuration,
                                  serviceFee, deposit, rentPrice, totalPayment)
}