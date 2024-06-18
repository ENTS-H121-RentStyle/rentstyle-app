package com.example.rentstyle.model.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.rentstyle.model.local.LocalProduct
import com.example.rentstyle.model.remote.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val apiService: ApiService,
    private val productDao: ProductDAO
) : RemoteMediator<Int, LocalProduct>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocalProduct>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                } else {
                    state.pages.size + 1
                }
            }
        }

        return try {
            val pageSize = state.config.pageSize
            val userId = getUserIdFromPreferences() // Mendapatkan user ID dari SharedPreferences
            val response = apiService.getRecommendations(userId, page)
            if (response.isSuccessful) {
                val products = response.body()?.products.orEmpty()
                productDao.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        productDao.clearAll()
                    }
                    productDao.insertAll(products.map { it.toLocalProduct() })
                }
                MediatorResult.Success(endOfPaginationReached = products.isEmpty())
            } else {
                MediatorResult.Error(HttpException(response))
            }
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private fun getUserIdFromPreferences(): String {
        // Implementasi ini sesuaikan dengan cara Anda mengambil user ID dari SharedPreferences
        // Contoh sederhana:
        // val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        // return sharedPreferences.getString("userId", "") ?: ""
        return "123456" // Dummy implementation, ganti dengan yang sesuai
    }
}
