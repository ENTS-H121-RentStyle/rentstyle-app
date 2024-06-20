package com.example.rentstyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.ProductRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class OrderViewModel(private val productRepository: ProductRepository): ViewModel() {
    var orderId = MutableLiveData<String>()
    val productId = MutableLiveData<String>()

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

    suspend fun makeReview(orderId: RequestBody,
                           productId: RequestBody,
                           userId: RequestBody,
                           sellerId: RequestBody,
                           rating: RequestBody,
                           review: RequestBody,
                           file: MultipartBody.Part) = productRepository
                               .giveProductReview(orderId,
                                   productId, userId, sellerId,
                                   rating, review, file)
}