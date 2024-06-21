package com.example.rentstyle.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.database.ProductDatabase
import com.example.rentstyle.model.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductRepository(
    private val database: ProductDatabase,
    private val apiService: ApiService
) {
    private val statusResult = MediatorLiveData<StatusResult>()

    suspend fun getProductByQuery(q: String, page: Int): List<Product> {
        return try {
            val response = apiService.getProductByKeyword(q, page)

            if (response.isSuccessful) {
                response.body()?.products ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
    suspend fun getFilteredProducts(filter: String, limit: Int, page: Int): List<Product> {
        return try {
            val response = apiService.getFilteredProducts(filter, limit, page)

            if (response.isSuccessful) {
                response.body()?.products ?: emptyList()
            } else {
                emptyList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun uploadProduct(
        productName: RequestBody,
        sellerId: RequestBody,
        category: RequestBody,
        size: RequestBody,
        image: MultipartBody.Part,
        color: RequestBody,
        desc: RequestBody,
        rentPrice: RequestBody,
        productPrice: RequestBody
    ): LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading
        return try {
            val response = apiService.createNewProduct(
                productName, sellerId, category, size, image,
                color, desc, rentPrice, productPrice
            )
            if (response.code() == 201) {
                statusResult.value = StatusResult.Success("Success adding new product")
            } else {
                statusResult.value = StatusResult.Error("Failed adding new product")
            }
            statusResult
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
            statusResult
        }
    }

    suspend fun deleteShoppingCartItem(id: String): LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading

        return try {
            val response = apiService.deleteShoppingCartItem(id)
            if (response.code() == 200) {
                statusResult.value = StatusResult.Success("Success deleted item from cart")
            } else {
                statusResult.value = StatusResult.Error("Fail deleted item from cart")
            }
            statusResult
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
            statusResult
        }
    }

    suspend fun updateShoppingCartItem(id: String, duration: RequestBody): LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading

        return try {
            val response = apiService.updateCartDuration(id, duration)
            if (response.code() == 200) {
                statusResult.value = StatusResult.Success("")
            } else {
                statusResult.value = StatusResult.Error("Fail deleted item from cart")
            }
            statusResult
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
            statusResult
        }
    }

    suspend fun makeOrder(productId: RequestBody,
                          userId: RequestBody,
                          orderDate: RequestBody,
                          returnDate: RequestBody,
                          rentDuration: RequestBody,
                          serviceFee: RequestBody,
                          deposit: RequestBody,
                          rentPrice: RequestBody,
                          totalPayment: RequestBody): LiveData<StatusResult>{
        statusResult.value = StatusResult.Loading

        return try {
            val response = apiService.makeOrder(productId, userId, orderDate, returnDate, rentDuration, serviceFee, deposit, rentPrice, totalPayment)
            if (response.code() == 201) {
                statusResult.value = StatusResult.Success("Success make an order")
            } else {
                statusResult.value = StatusResult.Error("Fail to make an order")
            }
            statusResult
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
            statusResult
        }
    }

    suspend fun giveProductReview(orderId: RequestBody,
                                  productId: RequestBody,
                                  userId: RequestBody,
                                  sellerId: RequestBody,
                                  rating: RequestBody,
                                  review: RequestBody,
                                  file: MultipartBody.Part): LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading

        return try {
            val response = apiService.makeProductReview(orderId, productId, userId, sellerId, rating, review, file)
            if (response.code() == 201) {
                statusResult.value = StatusResult.Success("Thank you for your review")
            } else if (response.code() == 400) {
                statusResult.value = StatusResult.Error("You have reviewed this product")
            }
            else {
                statusResult.value = StatusResult.Error("Fail to make a review")
            }
            statusResult
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
            statusResult
        }
    }
}
