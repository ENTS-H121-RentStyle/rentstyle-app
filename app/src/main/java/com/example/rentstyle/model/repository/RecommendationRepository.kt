package com.example.rentstyle.model.repository

import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class RecommendationRepository(private val apiService: ApiService) {

    suspend fun getRecommendationProducts(userId: String, page: Int): List<Product> {
        return try {
            val response = apiService.getRecommendations(userId, page)
            if (response.isSuccessful) {
                response.body()?.products ?: emptyList()
            } else {
                // Handle error if needed
                emptyList()
            }
        } catch (e: IOException) {
            // Handle IO exception
            emptyList()
        } catch (e: HttpException) {
            // Handle HTTP exception
            emptyList()
        }
    }
}
