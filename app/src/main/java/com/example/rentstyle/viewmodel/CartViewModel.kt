package com.example.rentstyle.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.ProductRepository
import okhttp3.RequestBody

class CartViewModel (private val productRepository: ProductRepository): ViewModel() {
    suspend fun deleteItemInCart(id: String) = productRepository.deleteShoppingCartItem(id)

    suspend fun updateItemInCart(id: String, duration: RequestBody) = productRepository.updateShoppingCartItem(id, duration)
}